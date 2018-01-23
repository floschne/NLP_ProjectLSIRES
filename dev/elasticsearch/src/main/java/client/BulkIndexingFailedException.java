package client;

import java.util.List;

class BulkIndexingFailedException extends Exception {

    private List<Exception> basket;

    public BulkIndexingFailedException(List<Exception> basket) {
        this.basket = basket;
    }

    public List<Exception> getBasket() {
        return basket;
    }
}
