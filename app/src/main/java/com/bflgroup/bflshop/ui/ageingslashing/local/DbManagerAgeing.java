package com.bflgroup.bflshop.ui.ageingslashing.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;
import com.bflgroup.bflshop.ui.ageingslashing.AgeingSlashingGlobal;
import com.bflgroup.bflshop.ui.ageingslashing.model.AgeingSlashingItemsScan;
import com.bflgroup.bflshop.ui.ageingslashing.model.AgeingSlashingScanDetailsGlobal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DbManagerAgeing {

    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private DBConnection dbConnection = new DBConnection();
    private AgeingSlashingGlobal objAgeingSlashingGlobal = AgeingSlashingGlobal.getInstance();
    private AgeingSlashingScanDetailsGlobal objAgeingSlashingScanDetailsGlobal = AgeingSlashingScanDetailsGlobal.getInstance();

    ArrayList<AgeingSlashingItemsScan> listAgeingSlashingItemsScan = new ArrayList<AgeingSlashingItemsScan>();

    DecimalFormat formatterNoDecimal = new DecimalFormat("#########");

    private boolean b_Result;
    private ResultSet rs;

    private SQLiteDatabase sqlDB;
    static final String dBName = "AGEING";
    static final int DBVersion = 2;
    static final String createTableRFIDTransfer = "create table RFIDTransfer (trfNo text,trfDate text,trfDateDiff number)";
    static final String createTableAgeingItems = "create table AgeingItems (BatchNO text,Itemcode text,ItemName text,Department text,Division text,GroupCode text,GroupName text," +
            "CurrPrice float,NewPrice float,LabelType text,SlashingType text,WasPrice float)";
    static final String createTableAgeingItemsLog = "create table AgeingItemsLogNew (BatchNO text,SrId text,Itemcode text,ItemName text,Department text,Division text,GroupCode text,GroupName text," +
            "CurrPrice float,NewPrice float,LabelType text,SlashingType text,WasPrice float,TrfNo text,TrfDate text,ScanDate text,ScanTime text,NewBarcode text,NewTrfNo text,Export text)";
    static final String createTableAutoSlno="create table AutoSlno(slno number,trfno text)";
    static final String createTableScanLog="create table scanAgeingLog(SrId text,BatchNo text,ScanBarCode text,ScanDate text,ScanTime text,Remarks text,Export text)";

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("DbManagerRFIDTransfer.checkConnection : Connection error");
                return false;
            }
        }
        if (dbConnection.checkCloudConnectionClosed()==false) {
            objGlobal.setCloudDbName("BFLDATA");
            b_Result = dbConnection.connectCloudDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("DbManagerAgeing.validateShopTransfer : Cloud Connection error 1.1");
                return false;
            }
        }
        return true;
    }

    static class DatabaseHelperUser extends SQLiteOpenHelper {
        Context context;

        DatabaseHelperUser(Context context) {
            super(context, dBName, null, DBVersion);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(createTableRFIDTransfer);
            Toast.makeText(context, "RFIDTransfer Created", Toast.LENGTH_LONG).show();
            db.execSQL(createTableAgeingItems);
            Toast.makeText(context, "AgeingItems Created", Toast.LENGTH_LONG).show();
            db.execSQL(createTableAgeingItemsLog);
            Toast.makeText(context, "AgeingItemsLogNew Created", Toast.LENGTH_LONG).show();
            db.execSQL(createTableAutoSlno);
            Toast.makeText(context, "AutoSlno Created", Toast.LENGTH_LONG).show();
            db.execSQL(createTableScanLog);
            Toast.makeText(context, "scanAgeingLog Created", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /*db.execSQL("drop table if exists StockTaking");
            onCreate(db);*/
        }
    }

    public DbManagerAgeing(Context context) {
        DbManagerAgeing.DatabaseHelperUser db = new DbManagerAgeing.DatabaseHelperUser(context);
        sqlDB = db.getWritableDatabase();
    }

    public long insertRFIDTransfer(ContentValues values) {
        long id = sqlDB.insert("RFIDTransfer", "", values);
        return id; //fail 0 or less
    }

    public long insertDataAgeingItems(ContentValues values) {
        long id = sqlDB.insert("AgeingItems", "", values);
        return id; //fail 0 or less
    }

    public long insertDataAgeingItemsLog(ContentValues values) {
        long id = sqlDB.insert("AgeingItemsLogNew", "", values);
        return id; //fail 0 or less
    }

    public long insertDataAutoSlno(ContentValues values) {
        long id = sqlDB.insert("AutoSlno", "", values);
        return id; //fail 0 or less
    }

    public long insertDataScanAgeingLog(ContentValues values) {
        long id = sqlDB.insert("scanAgeingLog", "", values);
        return id; //fail 0 or less
    }

    public Cursor queryRFIDTransfer(String[] projection, String selection, String[] selectionArgs, String groupBy, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("RFIDTransfer");
        Cursor cursor = qb.query(sqlDB, projection, selection, selectionArgs, groupBy, null, sortOrder);
        return cursor;
    }

    public Cursor queryAgeingItems(String[] projection, String selection, String[] selectionArgs, String groupBy, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("AgeingItems");
        Cursor cursor = qb.query(sqlDB, projection, selection, selectionArgs, groupBy, null, sortOrder);
        return cursor;
    }

    public Cursor queryAgeingItemsLog(String[] projection, String selection, String[] selectionArgs, String groupBy, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("AgeingItemsLogNew");
        Cursor cursor = qb.query(sqlDB, projection, selection, selectionArgs, groupBy, null, sortOrder);
        return cursor;
    }

    public Cursor queryScanAgeingLog(String[] projection, String selection, String[] selectionArgs, String groupBy, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("ScanAgeingLog");
        Cursor cursor = qb.query(sqlDB, projection, selection, selectionArgs, groupBy, null, sortOrder);
        return cursor;
    }

    public Cursor queryAutoSlno(String[] projection, String selection, String[] selectionArgs, String groupBy, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("AutoSlno");
        Cursor cursor = qb.query(sqlDB, projection, selection, selectionArgs, groupBy, null, sortOrder);
        return cursor;
    }

    public int deleteRFIDTransfer(String selection, String[] selectionArgs) {
        int count = sqlDB.delete("RFIDTransfer", selection, selectionArgs);
        return count;
    }

    public boolean deleteAllRFIDTransfer() {
        try {
            int del = deleteRFIDTransfer(null, null);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:deleteAllRFIDTransfer:" + ex.toString());
            return false;
        }
        return true;
    }

    public int deleteAgeingItems(String selection, String[] selectionArgs) {
        int count = sqlDB.delete("AgeingItems", selection, selectionArgs);
        return count;
    }

    public int deleteAutoSlno(String selection, String[] selectionArgs) {
        int count = sqlDB.delete("AutoSlno", selection, selectionArgs);
        return count;
    }

    public boolean deleteAllAgeingItems() {
        try {
            int del = deleteAgeingItems(null, null);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:AgeingItems:" + ex.toString());
            return false;
        }
        return true;
    }

    public int updateAgeingItemsLogNew(ContentValues values, String Selection, String[] SelectionArgs) {
        int count = sqlDB.update("AgeingItemsLogNew", values, Selection, SelectionArgs);
        return count;
    }

    public int updateAgeingItemsScanLog(ContentValues values, String Selection, String[] SelectionArgs) {
        int count = sqlDB.update("scanAgeingLog", values, Selection, SelectionArgs);
        return count;
    }

    public boolean newSlnoUpdate(){
        try {
            deleteAutoSlno(null,null);
            ContentValues values = new ContentValues();
            values.put("slno",objAgeingSlashingGlobal.getPdaPrefixSn());
            values.put("trfno","");
            long id = insertDataAutoSlno(values);
            if (id <= 0) {
                objGlobal.setErrorMessage("DbManagerRFIDTransfer:newSlnoUpdate:Local DB Insert error");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingSlashingControl:newSlnoUpdate:" + ex.toString());
            return false;
        }
    }

    public boolean loadRFIDTransfer() {
        if (!checkConnection()) {
            objGlobal.setErrorMessage("DbManagerRFIDTransfer:loadRFIDTransfer : Connection error");
            return false;
        }
        try {
            if (!deleteAllRFIDTransfer()) {
                return false;
            }
            rs = dbConnection.getResultSet("select TrfNo,TrfDate=convert(varchar,TrfDate,103),diff=DATEDIFF(DAY,TrfDate,getdate()) from RFIDTransfer union all " +
                    "select TrfNo='T'+sn,TrfDate=convert(varchar,pricedate,103),diff=DATEDIFF(DAY,pricedate,getdate()) from trfsalespriceageing where invoiceno=''", objGlobal.getConnection());
            while (rs.next()) {
                ContentValues values = new ContentValues();
                values.put("TrfNo", rs.getString("TrfNo"));
                values.put("TrfDate", rs.getString("TrfDate"));
                values.put("trfDateDiff", rs.getInt("diff"));
                long id = insertRFIDTransfer(values);
                if (id <= 0) {
                    objGlobal.setErrorMessage("DbManagerRFIDTransfer:loadRFIDTransfer:Local DB Insert error");
                    return false;
                }
            }
            return loadTotalRfidTransfer();
        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingSlashingControl:loadRFIDTransfer:" + ex.toString());
            return false;
        }
    }

    public boolean loadAgeingItems(String batchNo) {
        if (!checkConnection()) {
            objGlobal.setErrorMessage("DbManagerAgeingItems:loadAgeingItems : Connection error");
            return false;
        }
        try {
            if (!deleteAllAgeingItems()) {
                return false;
            }
            rs = dbConnection.getResultSet("select BatchNO,Itemcode,ItemName,Department,Division,GroupCode,GroupName,CurrPrice,NewPrice,LabelType," +
                    "SlashingType,WasPrice from AgeingItems where BatchNo='" + batchNo + "'", objGlobal.getConnection());
            while (rs.next()) {
                ContentValues values = new ContentValues();
                values.put("BatchNO", rs.getString("BatchNO"));
                values.put("Itemcode", rs.getString("Itemcode"));
                values.put("ItemName", rs.getString("ItemName"));
                values.put("Department", rs.getString("Department"));
                values.put("Division", rs.getString("Division"));
                values.put("GroupCode", rs.getString("GroupCode"));
                values.put("GroupName", rs.getString("GroupName"));
                values.put("CurrPrice", rs.getString("CurrPrice"));
                values.put("NewPrice", rs.getString("NewPrice"));
                values.put("LabelType", rs.getString("LabelType"));
                values.put("SlashingType", rs.getString("SlashingType"));
                values.put("WasPrice", rs.getString("WasPrice"));
                long id = insertDataAgeingItems(values);
                if (id <= 0) {
                    objGlobal.setErrorMessage("DbManagerAgeingItems:loadAgeingItems:Local DB Insert error");
                    return false;
                }
            }
            return loadTotalAgeingItems();
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeingItems:loadAgeingItems:" + ex.toString());
            return false;
        }
    }

    public boolean exportToMainServer(String BatchNO) {
        if (!checkConnection()) {
            objGlobal.setErrorMessage("DbManagerRFIDTransfer:exportToMainServer : Connection error");
            return false;
        }
        String Srid = "", Itemcode = "", ItemName = "", Department = "", Division = "", GroupCode = "", GroupName = "", LabelType = "", SlashingType = "", ScanBarCode="";
        String TrfNo = "", TrfDate = "", ScanDate = "", ScanTime = "", NewBarcode = "", IDNo = "", NewTrfNo ="",Remarks="";
        float CurrPrice = 0, NewPrice = 0, WasPrice = 0;
        int tCnt=0;
        String[] projection = {" * "};
        try {
            Cursor cursorScanAgeingLog = queryScanAgeingLog(projection, "Export='N'", null, null, null);
            if (cursorScanAgeingLog.moveToFirst()) {
                do {
                    Srid = cursorScanAgeingLog.getString(0);
                    ScanBarCode = cursorScanAgeingLog.getString(2);
                    ScanDate = cursorScanAgeingLog.getString(3);
                    ScanTime = cursorScanAgeingLog.getString(4);
                    Remarks = cursorScanAgeingLog.getString(5);
                    b_Result = dbConnection.insertUpdate("insert into scanAgeingLog(SrId,BatchNo,ScanBarCode,ScanDate,ScanTime,Remarks,username) values('" + Srid + "','" + BatchNO + "'," +
                            "'" + ScanBarCode + "','" + ScanDate + "','" + ScanTime + "','" + Remarks + "','" + objGlobal.getUserName() + "')", objGlobal.getConnection());
                    if (b_Result == false) {
                        return false;
                    }
                    ContentValues values = new ContentValues();
                    values.put("Export", "Y");
                    if (updateAgeingItemsScanLog(values, "Srid='" + Srid + "'", null) == 0) {
                        objGlobal.setErrorMessage("DbManagerAgeing:exportToMainServer:updateAgeingItemsLog:Error");
                        return false;
                    }
                } while (cursorScanAgeingLog.moveToNext());
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:exportToMainServer:ex2:" + ex.toString());
            return false;
        }

        try {
            Cursor cursorAgeingItemsLog = queryAgeingItemsLog(projection, "Export='N'", null, null, null);
            String entryNo = getLatestEno();
            objGlobal.getConnection().setAutoCommit(false);
            if (cursorAgeingItemsLog.moveToFirst()) {
                b_Result = dbConnection.insertUpdate("insert into PriceheaderAgeing(EntryNo,EntryDate,EntryType,CostCode,SubLocCode,Remarks,UserId,Delivery,TrfNo1,TrfNo2,LabelType,AgeingOther) values ( '" +
                        entryNo + "','" + objGlobal.getServerDate() + "'," + "'M'," + objPosGlobal.getCostCode() + "," + objPosGlobal.getLocCode() + ",'Remarks'," + objGlobal.getUserId() + ",'N','','' ,'','Manual')", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                do {
                    Srid = cursorAgeingItemsLog.getString(1);
                    Itemcode = cursorAgeingItemsLog.getString(2);
                    ItemName = cursorAgeingItemsLog.getString(3);
                    Department = cursorAgeingItemsLog.getString(4);
                    Division = cursorAgeingItemsLog.getString(5);
                    GroupCode = cursorAgeingItemsLog.getString(6);
                    GroupName = cursorAgeingItemsLog.getString(7);
                    CurrPrice = cursorAgeingItemsLog.getFloat(8);
                    NewPrice = cursorAgeingItemsLog.getFloat(9);
                    LabelType = cursorAgeingItemsLog.getString(10);
                    SlashingType = cursorAgeingItemsLog.getString(11);
                    WasPrice = cursorAgeingItemsLog.getFloat(12);
                    TrfNo = cursorAgeingItemsLog.getString(13);
                    TrfDate = cursorAgeingItemsLog.getString(14);
                    ScanDate = cursorAgeingItemsLog.getString(15);
                    ScanTime = cursorAgeingItemsLog.getString(16);
                    NewBarcode = cursorAgeingItemsLog.getString(17);
                    NewTrfNo = cursorAgeingItemsLog.getString(18);
                    IDNo = "";
                    b_Result = dbConnection.insertUpdate("insert into AgeingItemsLogNew(BatchNO,Srid,Itemcode,ItemName,Department,Division,GroupCode,GroupName,CurrPrice,NewPrice,LabelType,SlashingType,WasPrice,TrfNo,ScanDate," +
                            "ScanTime,NewBarcode,NewTrfNo,Export,DeviceId,UserId) values ('" + BatchNO + "','" + Srid + "','" + Itemcode + "','" + ItemName + "','" + Department + "','" + Division + "','" + GroupCode + "','" + GroupName + "'," +
                            "" + CurrPrice + "," + NewPrice + ",'" + LabelType + "','" + SlashingType + "'," + WasPrice + ",'" + TrfNo + "','" + ScanDate + "','" + ScanTime + "','" + NewBarcode + "','" + NewTrfNo + "'," +
                            "'Y','" + objGlobal.getDeviceName() + "'," + objGlobal.getUserId() + ")", objGlobal.getConnection());
                    if (b_Result == false) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                    b_Result = dbConnection.insertUpdate("insert into PriceDetailAgeing(EntryNo,ItemCode,Quantity,SalesPrice,NewPrice,TrfNo,TrfDate,IDNo,Status,RFId) values ('" + entryNo + "'," +
                            "'" + Itemcode + "',1," + CurrPrice + "," + NewPrice + ",'" + TrfNo + "','" + TrfDate + "','" + IDNo + "','" + SlashingType + "','')", objGlobal.getConnection());
                    if (b_Result == false) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                    b_Result = dbConnection.insertUpdate("Insert into TrfSalesPriceAgeing(CostCode,Itemcode,TrfNo,OldPrice,SuggPrice,NewPrice,Remarks,PriceDate,DispDate,SN,PriceType,Invoiceno,InvoiceDate,RFID) " +
                            "values('" + objPosGlobal.getCostCode() + "','" + Itemcode + "','" + TrfNo + "'," + CurrPrice + "," + NewPrice + "," + NewPrice + ",'" + entryNo + "','" + objGlobal.getServerDate() + "'," +
                            "null,'" + IDNo + "','','',null,'')", objGlobal.getConnection());
                    if (b_Result == false) {
                        objGlobal.getConnection().rollback();
                        return false;
                    }
                    rs = dbConnection.getResultSet(" select * from oldsalesprice where itemcode='" + Itemcode + "' and costcode='" + objPosGlobal.getCostCode() + "' and salesrate=" + NewPrice, objGlobal.getConnection());
                    if (!rs.next()) {
                        b_Result = dbConnection.insertUpdate("insert into oldsalesprice values('" + objPosGlobal.getCostCode() + "','" + Itemcode + "'," + NewPrice + "," + NewPrice + ")", objGlobal.getConnection());
                        if (b_Result == false) {
                            objGlobal.getConnection().rollback();
                            return false;
                        }
                    }
                    ContentValues values = new ContentValues();
                    values.put("Export", "Y");
                    if (updateAgeingItemsLogNew(values, "Srid='" + Srid + "' and itemcode='" + Itemcode + "'" ,null) == 0) {
                        objGlobal.getConnection().rollback();
                        objGlobal.setErrorMessage("DbManagerAgeing:exportToMainServer:updateAgeingItemsLogNew:Error");
                        return false;
                    }
                } while (cursorAgeingItemsLog.moveToNext());
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("DbManagerAgeing:exportToMainServer:ex2:" + ex.toString());
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("DbManagerAgeing:exportToMainServer:ex3:" + e.toString());
                return false;
            }
            return false;
        }
    }

    public boolean saveScanToLocaldb(String BatchNo,String Itemcode,String ItemName,String Department,String Division,String GroupCode,String GroupName,float CurrPrice,float NewPrice,String LabelType,String SlashingType,float WasPrice,
                                     String TrfNo,String TrfDate,String NewBarcode,String NewTrfNo) {
        Date date = new Date();
        SimpleDateFormat cDateF = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat cTimeF = new SimpleDateFormat("HH:mm:ss");
        String cDate = cDateF.format(date);
        String cTime = cTimeF.format(date);
        String srid = String.valueOf(System.currentTimeMillis());
        try {
            ContentValues values = new ContentValues();
            values.put("BatchNO", BatchNo);
            values.put("srid", srid);
            values.put("Itemcode", Itemcode);
            values.put("ItemName", ItemName);
            values.put("Department", Department);
            values.put("Division", Division);
            values.put("GroupCode", GroupCode);
            values.put("GroupName", GroupName);
            values.put("CurrPrice", CurrPrice);
            values.put("NewPrice", NewPrice);
            values.put("LabelType", LabelType);
            values.put("SlashingType", SlashingType);
            values.put("WasPrice", WasPrice);
            values.put("TrfNo", TrfNo);
            values.put("TrfDate", TrfDate);
            values.put("ScanDate", cDate);
            values.put("ScanTime", cTime);
            values.put("NewBarcode", NewBarcode);
            values.put("NewTrfNo", NewTrfNo);
            values.put("Export", "N");
            long id = insertDataAgeingItemsLog(values);
            if (id <= 0) {
                objGlobal.setErrorMessage("DbManagerAgeing:saveScanToLocaldb : Data not inserted");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:saveScanToLocaldb : " + ex.toString());
            return false;
        }
    }

    public boolean saveScanLog(String batchNo,String scanCode,String remarks) {
        Date date = new Date();
        SimpleDateFormat cDateF = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat cTimeF = new SimpleDateFormat("HH:mm:ss");
        String cDate = cDateF.format(date);
        String cTime = cTimeF.format(date);
        String srid = String.valueOf(System.currentTimeMillis());
        try {
            ContentValues values = new ContentValues();
            values.put("SrId", srid);
            values.put("BatchNo", batchNo);
            values.put("ScanBarCode", scanCode);
            values.put("ScanDate", cDate);
            values.put("ScanTime", cTime);
            values.put("Remarks", remarks);
            values.put("Export", "N");
            long id = insertDataScanAgeingLog(values);
            if (id <= 0) {
                objGlobal.setErrorMessage("DbManagerAgeing:saveScanLog : Data not inserted");
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:saveScanLog : " + ex.toString());
            return false;
        }
        return true;
    }

    boolean loadTotalRfidTransfer() {
        try {
            objAgeingSlashingGlobal.setTotalRfidTransfer(0);
            String[] projection = {"count(*) as cnt"};
            Cursor cursor = queryRFIDTransfer(projection, null, null, null, null);
            if (cursor.moveToFirst()) {
                objAgeingSlashingGlobal.setTotalRfidTransfer(cursor.getInt(0));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:loadTotalRfidTransfer:" + ex.toString());
            return false;
        }
        return true;
    }

    boolean loadTotalAgeingItems() {
        try {
            objAgeingSlashingGlobal.setTotalRfidTransfer(0);
            String[] projection = {"count(*) as cnt"};
            Cursor cursor = queryAgeingItems(projection, null, null, null, null);
            if (cursor.moveToFirst()) {
                objAgeingSlashingGlobal.setTotalAgeingItemsImport(cursor.getInt(0));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:loadTotalAgeingItems:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean getAgeingItems(String itemcode, float oldPrice) {
        String selesctquery = "select * from AgeingItems where itemcode='" + itemcode + "' and currPrice=" + oldPrice;
        try {
            Cursor cursor = sqlDB.rawQuery(selesctquery, null);
            if (cursor.moveToFirst()) {
                objAgeingSlashingScanDetailsGlobal.setItemName(cursor.getString(2));
                objAgeingSlashingScanDetailsGlobal.setDepartment(cursor.getString(3));
                objAgeingSlashingScanDetailsGlobal.setDivision(cursor.getString(4));
                objAgeingSlashingScanDetailsGlobal.setGroupCode(cursor.getString(5));
                objAgeingSlashingScanDetailsGlobal.setGroupName(cursor.getString(6));
                objAgeingSlashingScanDetailsGlobal.setCurrPrice(cursor.getFloat(7));
                objAgeingSlashingScanDetailsGlobal.setNewPrice(cursor.getFloat(8));
                objAgeingSlashingScanDetailsGlobal.setLabelType(cursor.getString(9));
                objAgeingSlashingScanDetailsGlobal.setSlashingType(cursor.getString(10));
                objAgeingSlashingScanDetailsGlobal.setWasPrice(cursor.getFloat(11));
            } else {
                objGlobal.setErrorMessage("No data found");
                return false;
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
        return true;
    }

    public boolean validateTrfNo(String trfNo) {
        String selesctquery = "select * from RFIDTransfer where trfno='" + trfNo + "'";
        try {
            Cursor cursor = sqlDB.rawQuery(selesctquery, null);
            if (cursor.moveToFirst()) {
                objAgeingSlashingScanDetailsGlobal.setTrfDate(cursor.getString(1));
                objAgeingSlashingScanDetailsGlobal.setDateDiff(cursor.getInt(2));
            } else {
                objGlobal.setErrorMessage("validateAgingTrfNo:RFIDTransfer, No data found, "+trfNo);
                return false;
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
        return true;
    }

    public boolean validateAgeing(String itemcode,String department, String trfno, int dateDiff){
        if (trfno.startsWith("T")) {
            if (dateDiff < 40) {
                objGlobal.setErrorMessage("Not eligible for aging, department:" + department + ", " + dateDiff + " days");
                return false;
            }
        } else {
            if (department.contains("LFL")) {
                if (dateDiff < 80) {
                    objGlobal.setErrorMessage("Not eligible for aging, department:" + department + ", " + dateDiff + " days");
                    return false;
                }
            } else {
                if (dateDiff < 150) {
                    objGlobal.setErrorMessage("Not eligible for aging, department:" + department + ", " + dateDiff + " days");
                    return false;
                }
            }
        }
        return true;
    }

    ArrayList<AgeingSlashingItemsScan> loadLastScanItems() {
        try {
            listAgeingSlashingItemsScan.clear();
            String sqlQry = "select NewBarcode,CurrPrice,NewPrice,LabelType,Export from AgeingItemsLogNew order by ScanDate desc,ScanTime desc limit 50";
            Cursor cursor = sqlDB.rawQuery(sqlQry, null);
            if (cursor.moveToFirst()) {
                do {
                    listAgeingSlashingItemsScan.add(new AgeingSlashingItemsScan(cursor.getString(0), cursor.getFloat(1),
                            cursor.getFloat(2), cursor.getString(3), cursor.getString(4)));
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:loadLastScanItems:" + ex.toString());
            return null;
        }
        return listAgeingSlashingItemsScan;
    }

    public boolean loadScannedCountTotal(){
        try{
            objAgeingSlashingGlobal.setTotalScan(0);
            String[] projection={"count(*) as total"};
            Cursor cursor=queryAgeingItemsLog(projection,null,null,null,null);
            if (cursor.moveToFirst()) {
                objAgeingSlashingGlobal.setTotalScan(cursor.getDouble(cursor.getColumnIndex("total")));
            }
        } catch(Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:loadScannedCountTotal:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean loadScannedCountExportTotal(){
        try{
            objAgeingSlashingGlobal.setTotalScanExport(0);
            String[] projection={"count(*) as total"};
            Cursor cursor=queryAgeingItemsLog(projection,"Export='Y'",null,null,null);
            if (cursor.moveToFirst()) {
                objAgeingSlashingGlobal.setTotalScanExport(cursor.getDouble(cursor.getColumnIndex("total")));
            }
        } catch(Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:loadScannedCountTotal:" + ex.toString());
            return false;
        }
        return true;
    }

    String getLatestEno() {
        String grfNo = "";
        int autoSn = 0;
        try {
            String prefix = "";
            String yr = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            prefix = objPosGlobal.getShopLetter() + yr.substring(2, 4);
            rs = dbConnection.getResultSet("select en=isnull (max(right(entryno,5)),0)+1 from PriceheaderAgeing where entryno like '" + prefix + "%'", objGlobal.getConnection());
            if (rs.next()) {
                autoSn = Integer.parseInt(rs.getString("en").toString());
            }
            grfNo = prefix + String.format("%05d", autoSn);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:getLatestEno:" + ex.toString());
            return "";
        }
        return grfNo;
    }

    public boolean getNewTrfNoLocalDb() {
        try {
            objAgeingSlashingScanDetailsGlobal.setNewTrfNo("");
            objAgeingSlashingGlobal.setPdaPrefixSn(0);
            String[] projection = {"IFNULL(MAX(slno), 0)+1 as slno"};
            Cursor cursor = queryAutoSlno(projection, null, null, null, null);
            if (cursor.moveToFirst()) {
                objAgeingSlashingGlobal.setPdaPrefixSn(cursor.getDouble(cursor.getColumnIndex("slno")));
                objAgeingSlashingScanDetailsGlobal.setNewTrfNo("T" + objAgeingSlashingGlobal.getPdaPrefix() + formatterNoDecimal.format(cursor.getDouble(cursor.getColumnIndex("slno"))));
            } else {
                objGlobal.setErrorMessage("DbManagerAgeing:getLatestSlnoLocalDb:error - please check");
                return false;
            }
            if(objAgeingSlashingScanDetailsGlobal.getNewTrfNo().isEmpty() || objAgeingSlashingGlobal.getPdaPrefixSn()==0){
                objGlobal.setErrorMessage("DbManagerAgeing:getLatestSlnoLocalDb:error - getNewTrfNo() is empty");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:getLatestSlnoLocalDb:ex:" + ex.toString());
            return false;
        }
    }

    public boolean checkNewTrfNoLocalDb() {
        try {
            String[] projection = {" * "};
            Cursor cursor = queryAutoSlno(projection, null, null, null, null);
            if (cursor.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:checkNewTrfNoLocalDb:ex:" + ex.toString());
            return false;
        }
    }

}
