from typing import Optional
from fastapi import HTTPException, status
from sqlalchemy.orm.session import Session

from db.models import User, Coin,  LikeList
import requests
import json


def registerUser(username: str, nickname: str, db: Session):

    user = db.query(User).filter(User.username == username).first()

    if not user:
        new_user = User(username = username, nickname = nickname)
        db.add(new_user)
        db.commit()
        db.refresh(new_user)
        user = db.query(User).filter(User.username == username).first()

def postLikeCoin(username: str, coin_id: int, db: Session):
    user = db.query(User).filter(User.username == username).first()

    like = db.query(LikeList).filter(LikeList.user_id == user.id, LikeList.coin_id == coin_id).first()
    if like:
        return 'Exist!'
    new_like = LikeList(user_id = user.id, coin_id = coin_id)
    db.add(new_like)
    db.commit()
    db.refresh(new_like)

    return 'Success'

def deleteLikeCoin(username: str, coin_id: int, db: Session):
    user = db.query(User).filter(User.username == username).first()
    like = db.query(LikeList).filter(LikeList.coin_id == coin_id, LikeList.user_id == user.id)
    
    like.delete(synchronize_session=False)
    db.commit()

    return 'Success'

def getBinanceLikeList(username: str, db: Session):

    user = db.query(User).filter(User.username == username).first()
    likelist = db.query(Coin).join(LikeList, LikeList.coin_id == Coin.id).filter(LikeList.user_id == user.id, Coin.exchange_id == 0).all()

    result = []
    if likelist:
        binance_url = "https://api.binance.com/api/v3/ticker/24hr"
        headers = {"accept": "application/json"}
        res = requests.get(binance_url, headers=headers)
        res = json.loads(res.text)
        for coin in res:
            try:
                if "USDT" in coin['symbol'] and coin['symbol'].split('USDT')[0]:
                    binance_coin = db.query(Coin).join(LikeList, LikeList.coin_id == Coin.id)\
                        .filter(LikeList.user_id == user.id, Coin.api_name == coin['symbol'], Coin.exchange_id == 0).first()
                    if binance_coin:
                        islike = 1
                        
                        change = float(coin['priceChange'])
                        if change > 0: c = 1
                        elif change < 0: c = -1
                        else: c = 0
                        result.append({"id": binance_coin.id, "name": binance_coin.name, "name2": f"{binance_coin.name}/USDT", 
                                    "api_name" : binance_coin.api_name, "exchange" : binance_coin.exchange_id, 
                                    "change" : c, "like" : islike, "price" : coin['lastPrice']})
            except: return result
    return result

def getUpbitLikeList(username: str, db: Session):

    user = db.query(User).filter(User.username == username).first()
    likelist = db.query(Coin).join(LikeList, LikeList.coin_id == Coin.id).filter(LikeList.user_id == user.id, Coin.exchange_id == 1).all()

    result = []
    if likelist:
        params = []
        for upbit_coin in likelist:
            params.append(upbit_coin.api_name)
        
        upbit_url = "https://api.upbit.com/v1/ticker?markets="
        headers = {"accept": "application/json"}
        res = requests.get(upbit_url + ','.join(params), headers=headers)
        res = json.loads(res.text)
        try:
            for coin in res:
                upbit_coin = db.query(Coin).filter(Coin.exchange_id == 1, Coin.api_name == coin['market']).first()
                islike = 0
                if user:
                    like = db.query(LikeList).filter(LikeList.user_id == user.id, LikeList.coin_id == upbit_coin.id).first()
                    if like: 
                        islike = 1
                tmp = upbit_coin.api_name.split('-')
                change = coin['change']
                if change == 'RISE': c = 1
                elif change == 'FALL': c = -1
                else: c = 0
                result.append({"id": upbit_coin.id, "name" : upbit_coin.name, "name2": f"{tmp[1]}/KRW", "api_name" : upbit_coin.api_name, 
                                "exchange" : upbit_coin.exchange_id,  "change": c, "like" : islike, "price" : f"{coin['trade_price']}"})
        except: return result
    return result

def getBithumbLikeList(username: str, db: Session):

    user = db.query(User).filter(User.username == username).first()
    likelist = db.query(Coin).join(LikeList, LikeList.coin_id == Coin.id).filter(LikeList.user_id == user.id, Coin.exchange_id == 2).all()

    result = []
    if likelist:
        
        bithumb_url = "https://api.bithumb.com/public/ticker/ALL_KRW"
        headers = {"accept": "application/json"}
        res = requests.get(bithumb_url, headers=headers)
        res = json.loads(res.text)
        try:
            for coin in res["data"].keys():
                if coin != 'date':
                    bithumb_coin = db.query(Coin).join(LikeList, LikeList.coin_id == Coin.id)\
                        .filter(LikeList.user_id == user.id, Coin.api_name == coin, Coin.exchange_id == 2).first()
                    if bithumb_coin:
                        islike = 1
                        change = float(res['data'][coin]['fluctate_rate_24H'])
                        if change > 0: c = 1
                        elif change < 0: c = -1
                        else: c = 0
                        result.append({"id": bithumb_coin.id, "name" : bithumb_coin.api_name, "name2": f"{bithumb_coin.api_name}/KRW", 
                                    "api_name" : bithumb_coin.api_name,  "exchange" : bithumb_coin.exchange_id, 
                                    "change": c, "like" : islike, "price" : res['data'][coin]['closing_price']})
        except: return result
    return result

def binanceCoin(username: str, coin_api_name: str, db: Session):
    user = db.query(User).filter(User.username == username).first()
    result = []
    if coin_api_name:
        binance_url = "https://api.binance.com/api/v3/ticker/24hr?symbol="
        headers = {"accept": "application/json"}
        res = requests.get(binance_url + coin_api_name, headers=headers)
        try:
            coin = json.loads(res.text)
            binance_coin = db.query(Coin).filter(Coin.exchange_id == 0, Coin.api_name == coin['symbol']).first()
            islike = 0
            if user:
                like = db.query(LikeList).filter(LikeList.user_id == user.id, LikeList.coin_id == binance_coin.id).first()
                if like: 
                    islike = 1

            change = float(coin['priceChange'])
            if change > 0: c = 1
            elif change < 0: c = -1
            else: c = 0
            result.append({"id": binance_coin.id, "name": binance_coin.name, "name2": f"{binance_coin.name}/USDT", 
                        "api_name" : binance_coin.api_name, "exchange" : binance_coin.exchange_id, 
                        "change" : c, "like" : islike, "price" : coin['lastPrice']})
        except: return result
    else:
        binance_url = "https://api.binance.com/api/v3/ticker/24hr"
        headers = {"accept": "application/json"}
        res = requests.get(binance_url, headers=headers)
        res = json.loads(res.text)
        try:
            for coin in res:
                if "USDT" in coin['symbol'] and coin['symbol'].split('USDT')[0]:
                    binance_coin = db.query(Coin).filter(Coin.exchange_id == 0, Coin.api_name == coin['symbol']).first()
                    islike = 0
                    if user:
                        like = db.query(LikeList).filter(LikeList.user_id == user.id, LikeList.coin_id == binance_coin.id).first()
                        if like: 
                            islike = 1

                    change = float(coin['priceChange'])
                    if change > 0: c = 1
                    elif change < 0: c = -1
                    else: c = 0
                    result.append({"id": binance_coin.id, "name": binance_coin.name, "name2": f"{binance_coin.name}/USDT", 
                                "api_name" : binance_coin.api_name, "exchange" : binance_coin.exchange_id, 
                                "change" : c, "like" : islike, "price" : coin['lastPrice']})
        except: return result

    return result


def upbitCoin(username: str, coin_api_name: str, db: Session):
    user = db.query(User).filter(User.username == username).first()
    result = []

    if coin_api_name:
        upbit_url = "https://api.upbit.com/v1/ticker?markets="
        headers = {"accept": "application/json"}
        res = requests.get(upbit_url + coin_api_name, headers=headers)
        try:
            coin = json.loads(res.text)[0]
            upbit_coin = db.query(Coin).filter(Coin.exchange_id == 1, Coin.api_name == coin['market']).first()
            islike = 0
            if user:
                like = db.query(LikeList).filter(LikeList.user_id == user.id, LikeList.coin_id == upbit_coin.id).first()
                if like: 
                    islike = 1
            tmp = upbit_coin.api_name.split('-')
            change = coin['change']
            if change == 'RISE': c = 1
            elif change == 'FALL': c = -1
            else: c = 0
            result.append({"id": upbit_coin.id, "name" : upbit_coin.name, "name2": f"{tmp[1]}/KRW", "api_name" : upbit_coin.api_name, 
                            "exchange" : upbit_coin.exchange_id,  "change": c, "like" : islike, "price" : f"{coin['trade_price']}"})
           
        except: return result
    else:
        upbit_coin_list = db.query(Coin).filter(Coin.exchange_id == 1).all()
        params = []

        for upbit_coin in upbit_coin_list:
            params.append(upbit_coin.api_name)
        
        upbit_url = "https://api.upbit.com/v1/ticker?markets="
        headers = {"accept": "application/json"}
        res = requests.get(upbit_url + ','.join(params), headers=headers)
        res = json.loads(res.text)
        try:
            for coin in res:
                upbit_coin = db.query(Coin).filter(Coin.exchange_id == 1, Coin.api_name == coin['market']).first()
                islike = 0
                if user:
                    like = db.query(LikeList).filter(LikeList.user_id == user.id, LikeList.coin_id == upbit_coin.id).first()
                    if like: 
                        islike = 1
                tmp = upbit_coin.api_name.split('-')

                change = coin['change']
                if change == 'RISE': c = 1
                elif change == 'FALL': c = -1
                else: c = 0
                result.append({"id": upbit_coin.id, "name" : upbit_coin.name, "name2": f"{tmp[1]}/KRW", "api_name" : upbit_coin.api_name, 
                                    "exchange" : upbit_coin.exchange_id,  "change": c, "like" : islike, "price" : f"{coin['trade_price']}"})
        except: return result

    return result


def bithumbCoin(username: str, coin_api_name: str, db: Session):
    user = db.query(User).filter(User.username == username).first()
    result = []
    if coin_api_name:
        bithumb_url = "https://api.bithumb.com/public/ticker/"
        headers = {"accept": "application/json"}
        res = requests.get(bithumb_url + coin_api_name, headers=headers)
        try:
            coin = json.loads(res.text)["data"]
            if coin != 'date':
                bithumb_coin = db.query(Coin).filter(Coin.exchange_id == 2, Coin.api_name == coin_api_name).first()
                islike = 0
                if user:
                    like = db.query(LikeList).filter(LikeList.user_id == user.id, LikeList.coin_id == bithumb_coin.id).first()
                    if like: 
                        islike = 1
                change = float(coin['fluctate_rate_24H'])
                if change > 0: c = 1
                elif change < 0: c = -1
                else: c = 0
                result.append({"id": bithumb_coin.id, "name" : bithumb_coin.api_name, "name2": f"{bithumb_coin.api_name}/KRW", 
                            "api_name" : bithumb_coin.api_name,  "exchange" : bithumb_coin.exchange_id, 
                            "change": c, "like" : islike, "price" : coin['closing_price']})
        except: return result
    else:
        bithumb_url = "https://api.bithumb.com/public/ticker/ALL_KRW"
        headers = {"accept": "application/json"}
        res = requests.get(bithumb_url, headers=headers)
        res = json.loads(res.text)

        result = []
        try:
            for coin in res["data"].keys():
                if coin != 'date':
                    bithumb_coin = db.query(Coin).filter(Coin.exchange_id == 2, Coin.api_name == coin).first()
                    islike = 0
                    if user:
                        like = db.query(LikeList).filter(LikeList.user_id == user.id, LikeList.coin_id == bithumb_coin.id).first()
                        if like: 
                            islike = 1
                    change = float(res['data'][coin]['fluctate_rate_24H'])
                    if change > 0: c = 1
                    elif change < 0: c = -1
                    else: c = 0
                    result.append({"id": bithumb_coin.id, "name" : bithumb_coin.api_name, "name2": f"{bithumb_coin.api_name}/KRW", 
                                "api_name" : bithumb_coin.api_name,  "exchange" : bithumb_coin.exchange_id, 
                                "change": c, "like" : islike, "price" : res['data'][coin]['closing_price']})
        except: return result

    return result

# def initialize(db: Session):
#     binance_url = "https://api.binance.com/api/v3/ticker/24hr"
#     bithumb_url = "https://api.bithumb.com/public/ticker/ALL_KRW"
#     upbit_url = "https://api.upbit.com/v1/market/all?isDetails=true"

#     headers = {"accept": "application/json"}

#     # 바이낸스
#     response = requests.get(binance_url, headers=headers)
#     res = json.loads(response.text)
#     for coin in res:
#         if "USDT" in coin['symbol']:
#             if coin['symbol'].split('USDT')[0]:
#                 # print(f"{coin['symbol'].split('USDT')[0]}  {coin['symbol']}")
#                 new_coin = Coin(name = coin['symbol'].split('USDT')[0], api_name = coin['symbol'], exchange_id = 0)
#                 db.add(new_coin)
#                 db.commit()
#                 db.refresh(new_coin) 

#     # 업비트
#     response = requests.get(upbit_url, headers=headers)
#     res = json.loads(response.text)
#     for coin in res: # 업비트
#         if "KRW" in coin['market']:
#             # print(f"{coin['market']}  {coin['korean_name']} {coin['english_name']}")
#             new_coin = Coin(name = coin['english_name'], api_name = coin['market'], exchange_id = 1)
#             db.add(new_coin)
#             db.commit()
#             db.refresh(new_coin) 
    
#     # 빗썸
#     response = requests.get(bithumb_url, headers=headers)
#     res = json.loads(response.text)
#     for coin in res["data"].keys():
#         if coin != 'date':
#             new_coin = Coin(name = coin, api_name = coin, exchange_id = 2)
#             db.add(new_coin)
#             db.commit()
#             db.refresh(new_coin) 

#     pass