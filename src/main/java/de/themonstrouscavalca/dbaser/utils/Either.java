package de.themonstrouscavalca.dbaser.utils;

public record Either<L, R>(L left, R right){
    public static <L, R> Either<L, R> withLeft(L left){
        return new Either<>(left, null);
    }

    public static <L, R> Either<L, R> withRight(R right){
        return new Either<>(null, right);
    }

    public boolean isLeft(){
        return left != null;
    }

    public boolean isRight(){
        return right != null;
    }
}

