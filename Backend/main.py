from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from router import coin

from db import models
from db.database import engine

app = FastAPI()

app.include_router(coin.router)

models.Base.metadata.create_all(bind=engine)

origins = ["*"]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)