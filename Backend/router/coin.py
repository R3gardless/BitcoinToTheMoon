from typing import Optional
import requests
import json
from fastapi import HTTPException, status, APIRouter, Depends
from sqlalchemy.orm import Session
from db import db_coin
from db.database import get_db
from pydantic import BaseModel
router = APIRouter(
    prefix='/coin',
    tags=["coin"]
)



class Like(BaseModel):
    username : str
    coin_id: int

class DisLike(BaseModel):
    username : str
    coin_id : int

@router.get("/binance/like")
def getLikeBinance(username: str, nickname: str, db: Session = Depends(get_db)):

    db_coin.registerUser(username, nickname, db)
    return db_coin.getBinanceLikeList(username, db)

@router.get("/upbit/like")
def getLikeUpit(username: str, nickname: str, db: Session = Depends(get_db)):

    db_coin.registerUser(username, nickname, db)
    return db_coin.getUpbitLikeList(username, db)


@router.get("/bithumb/like")
def getLikeBithumb(username: str, nickname: str, db: Session = Depends(get_db)):

    db_coin.registerUser(username, nickname, db)
    return db_coin.getBithumbLikeList(username, db)

@router.post("")
def postLikeCoin(like: Like, db: Session = Depends(get_db)):
    return db_coin.postLikeCoin(like.username, like.coin_id, db)

@router.post("/delete")
def deleteLikeCoin(dislike: DisLike, db: Session = Depends(get_db)):
    return db_coin.deleteLikeCoin(dislike.username, dislike.coin_id, db)

@router.get("/binance")
def getBinanceCoin(username: str, coin_api_name: Optional[str] = None, db: Session = Depends(get_db)):
    
    return db_coin.binanceCoin(username, coin_api_name, db)
@router.get("/upbit")
def getUpbitCoin(username: str, coin_api_name: Optional[str] = None, db: Session = Depends(get_db)):
    
    return db_coin.upbitCoin(username, coin_api_name, db)
@router.get("/bithumb")
def getBithumbCoin(username: str, coin_api_name: Optional[str] = None, db: Session = Depends(get_db)):
    
    return db_coin.bithumbCoin(username, coin_api_name, db)

# @router.get("/initialCoin")
# def initialize(db: Session = Depends(get_db)):
#     return db_coin.initialize(db)