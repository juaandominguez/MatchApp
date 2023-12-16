package es.udc.ws.app.model.purchase;
import es.udc.ws.util.configuration.ConfigurationParametersManager;

    //// get the SqlPurchaseDao ////
public class SqlPurchaseDaoFactory {
    private final static String CLASS_NAME_PARAMETER = "SqlPurchaseDaoFactory.className";
    private static SqlPurchaseDao dao = null;

    private SqlPurchaseDaoFactory(){}

    @SuppressWarnings("rawtypes")
    private static SqlPurchaseDao getInstance(){
        try{
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlPurchaseDao) daoClass.getDeclaredConstructor().newInstance();
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    public synchronized static SqlPurchaseDao getDao(){
            if(dao == null) dao = getInstance();
        return dao;
    }
}
