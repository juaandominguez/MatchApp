package es.udc.ws.app.model.purchase;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
public interface SqlPurchaseDao {
    public Purchase create(Connection connection, Purchase purchase);

    public void update(Connection connection, Purchase purchase) throws InstanceNotFoundException;

    Purchase find(Connection connection, Long purchaseId) throws InstanceNotFoundException;

    public List<Purchase> findByUser(Connection connection, String userEmail) throws InstanceNotFoundException;



    public void remove(Connection connection, Long purchaseId) throws InstanceNotFoundException;
}
