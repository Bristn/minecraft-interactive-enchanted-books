package net.bristn.lectern;

public interface LecternAccess {
    int getTicks();

    float getNextPageAngle();

    float getPageAngle();

    float getFlipRandom();

    float getFlipTurn();

    float getBookRotation();

    float getTargetBookRotation();

    void setTicks(int ticks);

    void setNextPageAngle(float nextPageAngle);

    void setPageAngle(float pageAngle);

    void setFlipRandom(float flipRandom);

    void setFlipTurn(float flipTurn);

    void setBookRotation(float bookRotation);

    void setLastBookRotation(float lastBookRotation);

    void setTargetBookRotation(float targetBookRotation);

    boolean isTomeReaderLectern();

    void setIsTomeReaderLectern(boolean val);
}