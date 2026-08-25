package com.bflgroup.bflshop.ui.shopreturns.proxy;

import static com.bflgroup.bflshop.ui.shopreturns.ShopReturnsGlobal.setCount;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;
import com.bflgroup.bflshop.ui.shopreturns.AddScanItemDetails;
import com.bflgroup.bflshop.ui.shopreturns.ShopReturnsGlobal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ShopProxyReturnsControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    // private PosGlobal getObjPosGlobal = PosGlobal.getInstance();
    private boolean b_Result;
    private boolean s_Result;
    private ResultSet rs;
    int srno = 0;
    Integer Count = 0;
    ArrayList<String> arr1 = new ArrayList<>();

    private PosGlobal objPosGlobal = PosGlobal.getInstance();


    public ShopProxyReturnsControl() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("ShopReturnsControl : Local Connection error 1.0");
        }
        objGlobal.setCloudDbName("BFLDATA");
        b_Result = dbConnection.connectCloudDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("ShopReturnsControl : Cloud Connection error 1.0");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("ShopReturnsControl.checkConnection : Local Connection error 1.1");
                return false;
            }
        }
        if (dbConnection.checkCloudConnectionClosed() == false) {
            objGlobal.setCloudDbName("BFLDATA");
            b_Result = dbConnection.connectCloudDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("ShopReturnsControl.checkConnection : Cloud Connection error 1.1");
                return false;
            }
        }
        return true;
    }

    public boolean scanItemcode(String itemcode, String sprice, String category, String Shopname) throws SQLException {
        String query = "select * from itemmaster where itemcode = '" + itemcode + "'";
        Log.e("Query", query);
        ArrayList<String> shopfound = new ArrayList<String>();
        rs = dbConnection.getResultSet(query, objGlobal.getConnection());
        if (!rs.next()) {
            objGlobal.setErrorMessage("Itemcode is not valid");
            return false;
        }
        if (category.equals("Shop to Shop Transfer (Direct)")) {
            if (!sprice.equals("")) {
                rs = dbConnection.getResultSet("select ItemCode from SalesPrice where ItemCode='" + itemcode + "' and SalesRate=" + sprice + " union all " +
                        "select ItemCode from OldSalesPrice where ItemCode='" + itemcode + "' and SalesRate=" + sprice + " union all " +
                        "select ItemCode from PriceDetailAgeing where ItemCode='" + itemcode + "' and NewPrice=" + sprice, objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Salesprice is not valid");
                    return false;
                }
                rs = dbConnection.getResultSet("", objGlobal.getConnection());

                String Query = "select status from fabsmain..settings where descr = 'ALLOWEDSLASHED'";
                ResultSet rs1 = dbConnection.getResultSet(Query, objGlobal.getConnection());
                while (rs1.next()) {
                    shopfound.add(rs1.getString("status"));
                }

                if (!shopfound.contains(Shopname)) {
                    rs = dbConnection.getResultSet("select ItemCode from PriceDetailAgeing where ItemCode='" + itemcode + "' and NewPrice=" + sprice, objGlobal.getConnection());
                    if (rs.next()) {
                        objGlobal.setErrorMessage("This Item is Slashed - " + itemcode + " Price =  " + sprice);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean checktmpitem(String itemcode, String Category) throws SQLException {
        try {
            String query2 = "Select * from tmpshopproxyreturns where itemcode = '" + itemcode + "' and devicename = '" + objGlobal.getDeviceName() + "'";
            ResultSet rs2 = dbConnection.getResultSet(query2, objGlobal.getConnection());
            if (!rs2.next()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Log.e("error msgs", e.toString());
        }
        return false;
    }

    public boolean checkStocktakeDate() throws SQLException {
        try {
            String query2 = "SELECT stocktakedate FROM bfldata..stocktakeDates WHERE storename = '" + objPosGlobal.getShopName() + "'  AND DATEDIFF(DAY, GETDATE(), stocktakedate) BETWEEN 0 AND 2";
            ResultSet rs2 = dbConnection.getResultSet(query2, objGlobal.getCloudCon());
            if (rs2.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("ShopReturnsControl", e.toString());
        }
        return false;
    }


    public ArrayList<AddScanItemDetails> additemcode(String itemcode, String sprice, Context context, String Category, String itemRemarks) throws SQLException {
        ArrayList<AddScanItemDetails> arr;
        String query = "";
        arr = new ArrayList<AddScanItemDetails>();
//        if(!checktmpitem(itemcode, Category)) {


        if (sprice.equals("")) {
            query = "select isnull(openingdate, getdate()) as openingdate2,* from itemmaster im, Salesprice sp where im.itemcode = sp.itemcode and im.itemcode =   '" + itemcode + "'";
        } else {
            query = "select isnull(openingdate, getdate()) as openingdate2,SalesRate='" + sprice + "',* from itemmaster where itemcode =   '" + itemcode + "'";
        }
        Log.e("Query", query);
        try {
            if (Category.equals("DIC") && !itemcode.startsWith("DIC")) {
                    ShopReturnsGlobal.setMessage("This item is not a DIC item. Please select a DIC item.");
            } else if (Category.equals("PROXY") && !itemcode.startsWith("PRX")) {
                    ShopReturnsGlobal.setMessage("This item is not a PROXY item. Please select a PROXY item.");
            } else {
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    Float Qty = rs.getFloat("StockInHand") + 1;
                    SimpleDateFormat DateFor = new SimpleDateFormat("dd-MM-yyyy");
                    String date = "";
                    if (rs.getDate("OpeningDate2") != null) {

                        date = DateFor.format(rs.getDate("openingdate2"));
                    } else {
                        date = objGlobal.getServerDate();
                    }
                    String query1 = "Insert into tmpshopproxyreturns(Itemcode,itemdescription,qty,SalesPrice,devicename,category,shopname,ToPrint,description,ShortName,UnitCode,GroupCode,CatCode," +
                            "OpeningDate,itemRemarks) values('" + itemcode + "', '" + rs.getString("Description") + "',1, '" + rs.getString("SalesRate") + "', " +
                            "'" + objGlobal.getDeviceName() + "', '" + Category + "', '','N','" + rs.getString("description") + "', '" + rs.getString("ShortName") + "', " +
                            "'" + rs.getString("UnitCode") + "', '" + rs.getString("GroupCode") + "', '" + rs.getString("CatCode") + "', '" + date + "', '" + itemRemarks + "')";
                    Log.e("Insert1", query1);
                    if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                        okMessage("Alert", objGlobal.getErrorMessage(), context);
                    }
                    srno++;
                    arr.add(new AddScanItemDetails(srno, itemcode, rs.getString("Description"), rs.getFloat("StockInHand"), rs.getFloat("SalesRate")));
                    Count = setCount(Count + 1);
                    ShopReturnsGlobal.setMessage("Itemcode Added");
                } else {
                    ShopReturnsGlobal.setMessage("Item not found in the Salesprice");
                    //okMessage("Alert", "Item not found in Salesprice", context);
                }
            }


        } catch (Exception e) {
            okMessage("Alert", e.toString(), context);
        }

        arr = gettmpitem();
//        }

        arr = gettmpitem();

        return arr;
    }


    ArrayList<AddScanItemDetails> gettmpitem() throws SQLException {
        ArrayList<AddScanItemDetails> arrayList;
        int srno = 0;
        arrayList = new ArrayList<>();
        String querynew = "";
        querynew = "select * from tmpshopproxyreturns where  devicename = '" + objGlobal.getDeviceName() + "'";
        Log.e("Load Insert", querynew);
        ResultSet rs1 = dbConnection.getResultSet(querynew, objGlobal.getConnection());
        while (rs1.next()) {
            srno++;
            arrayList.add(new AddScanItemDetails(srno, rs1.getString("itemcode"), rs1.getString("itemdescription"), rs1.getFloat("qty"), rs1.getFloat("Salesprice")));
        }
        return arrayList;
    }

    public boolean InsertAddDetails(String remarks, String Category, String Shopname, Context context) throws SQLException {
        Log.e("Shopname", Shopname);

        objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer2 : Before Cloud");
        if (Category.isEmpty()) {
            objGlobal.setErrorMessage("Shop return category is empty");
            return false;
        }
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            objGlobal.setErrorMessage("ShopReturnControl.InsertAdd : Getserverdatetime");
            return false;
        }
        rs = dbConnection.getResultSet("select DataName,ShopName from bfldata.dbo.datasettings where Shopname='" + objPosGlobal.getShopName() + "'", objGlobal.getCloudCon());
        if (rs.next()) {


            objGlobal.setCloudDbName(rs.getString("DataName").toString());

        } else {
            objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer1 : Shop not found in datasettings, " + Shopname);
            return false;
        }
        objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer2 : Connected to the Cloud");

        Boolean result = dbConnection.connectCloudDb();
        if (result == false) {
            objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer2 : Connection error");
            return false;
        }

        try {
            String query = "", query2 = "", query6 = "";

            query = "select * from tmpshopproxyreturns where devicename = '" + objGlobal.getDeviceName() + "'";
            query2 = "select sum(qty) as qty, salesprice as salesprice,itemcode from tmpshopproxyreturns where devicename = '" + objGlobal.getDeviceName() + "' group by itemcode,salesprice";
            query6 = "select description, qty, ShortName, UnitCode,salesprice, itemcode, GroupCode,CatCode,OpeningDate,ToPrint from tmpshopproxyreturns where devicename = '" + objGlobal.getDeviceName() + "' group by itemcode,salesprice, qty, description, ShortName,UnitCode, GroupCode,CatCode,OpeningDate,ToPrint";

            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            ResultSet rs2 = dbConnection.getResultSet(query2, objGlobal.getConnection());
            ResultSet rs3 = dbConnection.getResultSet(query6, objGlobal.getConnection());


            objGlobal.getConnection().setAutoCommit(false);
            objGlobal.getCloudCon().setAutoCommit(false);

            if (!rs.next()) {
                okMessage("Alert", "Fields cannot be empty", context);
                return false;
            }
            String autoNo = AutoNo();
            ShopReturnsGlobal.setEntryNo(autoNo);


            if (!dbConnection.insertUpdate("Insert into storedetail (EntryNo,ItemCode,Quantity,SalesPrice,TrfNo,RFID,ItemRemarks) select '" + autoNo + "', ItemCode, qty, SalesPrice, '', '',itemRemarks from tmpshopproxyreturns where devicename = '" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                objGlobal.getCloudCon().setAutoCommit(true);
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }


            String transfer = ((Category.equals("Shop Transfer") ? "Y" : "N"));
            String Shop = ((Category.equals("Shop Transfer") ? Shopname : ""));


            if (!dbConnection.insertUpdate("Insert into storeheader (EntryNo,EntryDate,EntryType,CostCode,SubLocCode,Remarks,UserId,Delivery,TrfNo1,TrfNo2,shopname,Transfer)values ('" + autoNo + "','" + objGlobal.getServerDate() + "','D','" + objPosGlobal.getCostCode() + "','" + objPosGlobal.getLocCode() + "', 'ANDRPDA - " + remarks + "', '" + objGlobal.getUserId() + "', 'N', '" + Category.substring(0, Math.min(Category.length(), 15)) + "','', '" + Shopname + "','" + transfer + "') ", objGlobal.getConnection())) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                objGlobal.getCloudCon().setAutoCommit(true);
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }

            // String querynew1 = "Insert into storeheader (EntryNo,EntryDate,EntryType,CostCode,SubLocCode,Remarks,UserId,Delivery,TrfNo1,TrfNo2,shopname,Transfer, ShopFrom)values ('" + autoNo + "','" + objGlobal.getServerDate() + "','D','" + objPosGlobal.getCostCode() + "','" + objPosGlobal.getLocCode() + "', 'ANDRPDA - " + remarks + "', '" + objGlobal.getUserId() + "', 'N', '" + Category.substring(0, Math.min(Category.length(), 15)) + "','', '" + Shopname + "','" + transfer + "', '"+objPosGlobal.getShopName()+"') ";
            if (!dbConnection.insertUpdate("Insert into storeheader (EntryNo,EntryDate,EntryType,CostCode,SubLocCode,Remarks,UserId,Delivery,TrfNo1,TrfNo2,shopname,Transfer, ShopFrom)values ('" + autoNo + "','" + objGlobal.getServerDate() + "','D','" + objPosGlobal.getCostCode() + "','" + objPosGlobal.getLocCode() + "', 'ANDRPDA - " + remarks + "', '" + objGlobal.getUserId() + "', 'N', '" + Category.substring(0, Math.min(Category.length(), 15)) + "','', '" + Shopname + "','" + transfer + "', '" + objPosGlobal.getShopName() + "') ", objGlobal.getCloudCon())) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                objGlobal.getCloudCon().setAutoCommit(true);
                objGlobal.getConnection().setAutoCommit(true);
                //objGlobal.setErrorMessage("ShopReturnControl.InsertAdd : insert into storeheader cloud");
                return false;
            }


            while (rs2.next()) {
                String query3 = "Insert into storedetail (EntryNo,ItemCode,Quantity,RecQty, SalesPrice) values('" + autoNo + "','" + rs2.getString("itemcode") + "'," + rs2.getString("qty") + ",0," + rs2.getString("SalesPrice") + ") ";
                if (!dbConnection.insertUpdate(query3, objGlobal.getCloudCon())) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    objGlobal.getCloudCon().setAutoCommit(true);
                    objGlobal.getConnection().setAutoCommit(true);
                    objGlobal.setErrorMessage("ShopReturnControl.InsertAdd : insert into storedetail cloud");
                    return false;
                }
            }


            // insert into storeDetail(EntryNo,ItemCode,Quantity,RecQty, SalesPrice) select sum(qty) as qty, salesprice as salesprice,itemcode from tmpshopproxyreturns where devicename = '" + objGlobal.getDeviceName() + "' group by itemcode,salesprice
//            }

            while (rs3.next()) {
                String query7 = "select * from itemmaster where itemcode = '" + rs3.getString("itemcode") + "'";
                ResultSet rs4 = dbConnection.getResultSet(query7, objGlobal.getCloudCon());
                if (rs4.next()) {

                } else {
                    SimpleDateFormat DateFor = new SimpleDateFormat("dd-MM-yyyy");
                    String date = DateFor.format(rs3.getDate("OpeningDate"));
                    String query5 = "Insert into itemmaster (ItemCode,Description,ShortName,UnitCode,GroupCode,CatCode,OpeningDate,ToPrint) values ('" + rs3.getString("itemcode") + "','" + rs3.getString("Description") + "', '" + rs3.getString("ShortName") + "', '" + rs3.getString("UnitCode") + "', '" + rs3.getString("GroupCode") + "', '" + rs3.getString("CatCode") + "', '" + date + "', '" + rs3.getString("ToPrint") + "')";
                    if (!dbConnection.insertUpdate(query5, objGlobal.getCloudCon())) {
                        objGlobal.getCloudCon().rollback();
                        objGlobal.getConnection().rollback();
                        objGlobal.getCloudCon().setAutoCommit(true);
                        objGlobal.getConnection().setAutoCommit(true);
                        objGlobal.setErrorMessage("ShopReturnControl.InsertAdd : insert into itemmaster cloud");
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            okMessage("Alert", e.toString(), context);
        }
        objGlobal.getConnection().commit();
        objGlobal.getConnection().setAutoCommit(true);

        objGlobal.getCloudCon().commit();
        objGlobal.getCloudCon().setAutoCommit(true);
        return true;
    }


    public ArrayList<String> loadRemarks() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            String Query = "select Distinct remarks from bfldata..StoreReturnsremarks";
            ResultSet rs1 = dbConnection.getResultSet(Query, objGlobal.getCloudCon());
            while (rs1.next()) {
                arrayList.add(rs1.getString("remarks"));
            }
        } catch (Exception e) {
            Log.e("Alert", e.toString());
        }

        return arrayList;

    }

    public ArrayList<String> loadCategory() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            arrayList.add("--Select Category--");
            String Query = "EXEC dbo.GetStoreCategories '" + objPosGlobal.getShopName() + "', '" + objGlobal.getCountryCode() + "', 1";
            ResultSet rs1 = dbConnection.getResultSet(Query, objGlobal.getCloudCon());
            while (rs1.next()) {
                arrayList.add(rs1.getString("Category_name"));
            }
        } catch (Exception e) {
            Log.e("Alert", e.toString());
        }
        return arrayList;
    }

    public String loadCategoryName(String catName) {
        String Cat = "";
        try {
            String Query = "select Distinct Category_Code from bfldata.dbo.StoreEntryCategory where active ='Y' and Category_Name='" + catName + "' union all select Distinct Category_Code from " +
                    "bfldata.dbo.StoreEntryCategoryShopTransfer where ShopName='" + objPosGlobal.getShopName() + "' and Category_Name='" + catName + "'";
            ResultSet rs1 = dbConnection.getResultSet(Query, objGlobal.getCloudCon());
            if (rs1.next()) {
                Cat = rs1.getString("Category_Code");
            }
        } catch (Exception e) {
            Log.e("Alert", e.toString());
        }
        return Cat;
    }

    public String AutoNo() throws SQLException {
        String year = "";
        rs = dbConnection.getResultSet("select yr=right(year(getdate()),2)", objGlobal.getConnection());
        if (rs.next()) year = rs.getString("yr");
        String Prefix = objPosGlobal.getShopLetter() + "RTN/" + year + "/";
        rs = dbConnection.getResultSet("select max(right(entryno,5)) from storeheader where entryno like '" + Prefix + "%'", objGlobal.getConnection());
        int mNo = 1;
        if (rs.next()) {
            mNo = rs.getInt(1) + 1;
        }
        String AutoNo = Prefix + String.format("%05d", mNo);
        return AutoNo;
    }


    public ArrayList<String> ShopNames() throws SQLException {
        if (arr1.size() == 0) {
            String Query = "select Distinct ShopName from bfldata.dbo.datasettings where countrycode='" + objGlobal.getCountryCode() + "' and Country <> '' and shopname not like '%Online%'";
            ResultSet rs1 = dbConnection.getResultSet(Query, objGlobal.getCloudCon());
            while (rs1.next()) {
                arr1.add(rs1.getString("ShopName"));
            }
            Log.e("Shopnames", arr1 + "");
        }
        return arr1;
    }

    public boolean clearTable() {
        if (!checkConnection()) {
            return false;
        }
        try {

            if (!dbConnection.insertUpdate("delete from tmpshopproxyreturns where  devicename='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }

        } catch (Exception ex) {
            objGlobal.setErrorMessage("ShopreturnsControl:clearTable:" + ex.toString());
            return false;
        }
        Count = 0;
        Count = setCount(0);
        return true;
    }

    private void okMessage(String title, String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }


    public Integer getCount() throws SQLException {
        // Integer count = 0;
        String query = "select count(*) as count from tmpshopproxyreturns where devicename='" + objGlobal.getDeviceName() + "'";
        rs = dbConnection.getResultSet(query, objGlobal.getConnection());

        while (rs.next()) {
            Count = rs.getInt(1);
        }
        Count = setCount(Count);

        return Count;
    }

}
