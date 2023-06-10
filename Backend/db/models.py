from sqlalchemy import Column, String, Integer, ForeignKey
from sqlalchemy.orm import relationship
from db.database import Base

class User(Base):
    __tablename__ = 'user'

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(255), nullable=False, unique=True)
    nickname = Column(String(255), nullable=False)

    like = relationship('LikeList', back_populates='user', cascade="all, delete")

class LikeList(Base):
    __tablename__ = 'like'

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey('user.id'), nullable=False)
    coin_id = Column(Integer, ForeignKey('coin.id'), nullable=False)

    user = relationship('User', back_populates='like')
    coin = relationship('Coin', back_populates='like')
    
class Coin(Base):
    __tablename__ = 'coin'

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(255), nullable=False) # 표시 이름
    api_name = Column(String(255), nullable=False) # api 상 이름
    exchange_id = Column(Integer, nullable=False) # 0 = 바이낸스, 1 = 업비트, 2 = 빗썸

    like = relationship('LikeList', back_populates='coin', cascade="all, delete")
