from fastapi import FastAPI
from fastapi import FastAPI, status, Request
from fastapi.responses import JSONResponse, HTMLResponse
from fastapi.templating import Jinja2Templates
from fastapi.exceptions import RequestValidationError
from datetime import datetime 
import requests
import bisect
import asyncio
import json


def format_date(element: list):
    res = element
    res[0] = res[0][7:11] + '-' + months[res[0][0:3]] + '-' + res[0][4:6]
    return res
#extract_date


CURRENCYAPI_TOKEN = ...
try:
    with open("auth.json", 'r') as file:
        CURRENCYAPI_TOKEN = json.load(file)["FXRatesApi key"]
except FileNotFoundError as ex:
    raise FileNotFoundError("auth.json file not found")
except KeyError:
    raise KeyError('There is no "FXRatesApi key" field in auth.json file')

months = {
    "Jan": "01", "Feb": "02", "Mar": "03", "Apr": "04",
    "May": "05", "Jun": "06", "Jul": "07", "Aug": "08",
    "Sep": "09", "Oct": "10", "Nov": "11", "Dec": "12"
}

app = FastAPI()
templates = Jinja2Templates(directory="pages")


@app.exception_handler(404)
async def page_not_found_handler(request: Request, _):
    return templates.TemplateResponse("error.html", {"request": request, 
                                                     "error_info": "404 page not found"},
                                        status_code=status.HTTP_404_NOT_FOUND)
#page_not_found_handler


@app.exception_handler(RequestValidationError)
async def bad_query_handler(request: Request, _):
    return templates.TemplateResponse("error.html", {"request": request, 
                                                     "error_info": "Bad query"},
                                        status_code=status.HTTP_400_BAD_REQUEST)
#bad_query_handler


@app.get("/", response_class=HTMLResponse)
async def home_page(request: Request):
    return templates.TemplateResponse("home.html", {"request": request},
                                      status_code=status.HTTP_200_OK)
#home_page


@app.get("/submit", response_class=HTMLResponse)
async def submit_data(request: Request, skin: str, info_date: str, 
                      wear: str = 'Factory New', currency: str = 'USD'):

    # Get today's date
    # today_date_raw = datetime.now()
    # today_date = f"{today_date_raw.year}-{today_date_raw.month}-{today_date_raw.day}"

    # Gather information from all services
    full_skin_name = skin + " (" + wear + ")"
    temp = await asyncio.gather(get_skin_info(name=full_skin_name),
                   get_hist_currency(currency=currency, date=info_date),
                   get_valid_currency(currency=currency))
    skin_info, hist_currency_info, now_currency_info = temp

    # Check skin data
    if isinstance(skin_info, JSONResponse):
        error_info = skin_info.body.decode("utf-8")[1:-1]
        return templates.TemplateResponse("error.html", {"request": request, 
                                                         "error_info": error_info},
                                            status_code=skin_info.status_code)

    # Check historical currency rate data
    if isinstance(hist_currency_info, JSONResponse):
        error_info = hist_currency_info.body.decode("utf-8")[1:-1]
        return templates.TemplateResponse("error.html", {"request": request, 
                                                         "error_info": error_info},
                                            status_code=hist_currency_info.status_code)

    # Check today's currency rate data
    if isinstance(now_currency_info, JSONResponse):
        error_info = now_currency_info.body.decode("utf-8")[1:-1]
        return templates.TemplateResponse("error.html", {"request": request, 
                                                         "error_info": error_info},
                                            status_code=now_currency_info.status_code)

    # Extract currency rates data
    pu_rate =  hist_currency_info["rates"]["USD"]   # how much USD could I get for 1 [PLN] then
    up_rate = now_currency_info["rates"][currency]  # how much [PLN] can I get for 1 USD now

    # Modify skin list data
    skin_info = list(map(format_date, skin_info))
    dates = [datetime.strptime(el[0], "%Y-%m-%d") for el in skin_info]

    # Check if skin even existed on provided date
    info_date_it = bisect.bisect_right(dates, datetime.strptime(info_date, "%Y-%m-%d")) - 1
    if info_date_it < 0:
        error_info = f"First sale of '{full_skin_name}' took place on {skin_info[0][0]}, i.e. later than {info_date}."
        return templates.TemplateResponse("error.html", {"request": request,
                                                         "error_info": error_info},
                                            status_code=status.HTTP_400_BAD_REQUEST)

    # Extract skin-value rates data
    us_rate = 1 / skin_info[info_date_it][1]        # how much skins could I get for 1 USD then
    su_rate = max(0.01, skin_info[-1][1] / 1.15)    # how much USD can I get for one skin now

    # Calculate the income percent
    income_percent = pu_rate * up_rate * us_rate * su_rate - 1
    
    return templates.TemplateResponse("result.html", {"request": request, 
                                                      "skin": full_skin_name, 
                                                      "info_date": info_date, 
                                                      "currency": currency,
                                                      "income_percent": round(100*income_percent, 2)},
                                        status_code=status.HTTP_200_OK)
#submit_data


@app.get("/skin")
async def get_skin_info(name: str):
    try:
        url = "http://csgobackpack.net/api/GetItemPrice/?id=" + name + "&full=true"
        response = requests.get(url)
        dane = response.json()
        return dane
    
    except requests.exceptions.RequestException as e1:
        try:
            url = "http://csgobackpack.net/api/GetItemPrice/?id=AK-47 | The Empress (Factory New)"
            requests.get(url)
            return JSONResponse(status_code=status.HTTP_400_BAD_REQUEST, 
                                content=f"Item '{name}' has never been sold on Steam market.")
        except requests.exceptions.RequestException as e2:
            return JSONResponse(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, 
                                content="CSGO Backpack service is unavailable now.")
#get_skin_info


@app.get("/hist-currency")
async def get_hist_currency(date: str, currency: str = 'USD'):
    try: 
        url = "https://api.fxratesapi.com/historical?api_key=" + CURRENCYAPI_TOKEN \
                + "&date=" + date + "&currencies=USD" + "&places=8" + "&base=" + currency
        response = requests.get(url)
        dane = response.json()

        if not dane["success"]:
            return JSONResponse(status_code=status.HTTP_400_BAD_REQUEST, 
                                content=dane["description"])
        
        if currency == 'USD':
            dane["rates"]['USD'] = 1.0
        elif dane["rates"] == {}:
            return JSONResponse(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, 
                                content=f"No data about {currency} from {date}.")
        
        return dane
    
    except requests.exceptions.RequestException as e:
        return JSONResponse(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, 
                            content="FXRatesAPI service is unavailable now.")
#get_hist_currency


@app.get("/now-currency")
async def get_valid_currency(currency: str = 'USD'):
    try:
        url = "https://api.fxratesapi.com/latest?api_key=" + CURRENCYAPI_TOKEN \
                +"&currencies=" + currency + "&places=8&base=USD"
        response = requests.get(url)
        dane = response.json()

        if not dane["success"]:
            return JSONResponse(status_code=status.HTTP_400_BAD_REQUEST, 
                                content=dane["description"])
        return dane
    
    except requests.exceptions.RequestException as e:
        return JSONResponse(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, 
                            content="FXRatesAPI service is unavailable now.")
#get_valid_currency
