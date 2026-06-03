package com.bflgroup.bflshop.ui.discount;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddDiscountControl {

    DBConnection dbConnection = new DBConnection();
    Global objGlobal=Global.getInstance();
    boolean result;

    public boolean getDetail(String itemcode,float discPerc){
        String conResult="";
        float rate=0;
        try {
            if(itemcode=="" || itemcode==null){
                objGlobal.setErrorMessage("Blank Itemcode");
                return false;
            }
            if(dbConnection.checkConnectionClosed()==false){
                result=dbConnection.connectDb();
                if(result==false){
                    objGlobal.setErrorMessage("GrnTransferControl.validateTransferNumber : Connection error");
                    return false;
                }
            }
            String query = "select description,rate=(select top 1 salesrate from salesprice where itemcode=a.itemcode) from itemmaster a where itemcode='"+itemcode.toString()+"'";
            Statement stmt = objGlobal.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                rate=rs.getFloat("rate");
                dbConnection.insertUpdate("delete from tmpAddDiscItem where itemcode='"+itemcode+"' and userid="+objGlobal.getUserId(),objGlobal.getConnection());
                dbConnection.insertUpdate("insert into tmpAddDiscItem values('"+itemcode+"',"+rate+","+discPerc+","+objGlobal.getUserId()+")",objGlobal.getConnection());
                return true;
            }else{
                objGlobal.setErrorMessage("Invalid Itemcode");
                return false;
            }
        }catch (Exception ex) {
            objGlobal.setErrorMessage(ex.toString());
            return false;
        }
    }

    public boolean clearAllScan(){
        try{
            if(dbConnection.checkConnectionClosed()==false){
                result=dbConnection.connectDb();
                if(result==false){
                    objGlobal.setErrorMessage("Connection error");
                    return false;
                }
            }
            dbConnection.insertUpdate("delete from tmpAddDiscItem where userid="+objGlobal.getUserId(),objGlobal.getConnection());
            return true;
        }catch (Exception ex){
            objGlobal.setErrorMessage(ex.toString());
            return false;
        }
    }

    public boolean deleteSingle(String itemcode){
        try{
            if(dbConnection.checkConnectionClosed()==false){
                result=dbConnection.connectDb();
                if(result==false){
                    objGlobal.setErrorMessage("Connection error");
                    return false;
                }
            }
            dbConnection.insertUpdate("delete from tmpAddDiscItem where itemcode='"+itemcode+"' and userid="+objGlobal.getUserId(),objGlobal.getConnection());
            return true;
        }catch (Exception ex){
            objGlobal.setErrorMessage(ex.toString());
            return false;
        }
    }

    public boolean addDiscountSave(){
        String conResult="";
        try {
            if(dbConnection.checkConnectionClosed()==false){
                result=dbConnection.connectDb();
                if(result==false){
                    objGlobal.setErrorMessage("Connection error");
                    return false;
                }
            }
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(date);
            int getSno=getSno();
            if(getSno==0){
                objGlobal.setErrorMessage("SN is 0");
                return false;
            }
            dbConnection.insertUpdate("insert into AddDiscountDetail select "+getSno+",'"+formattedDate+"',itemcode,discper,userid " +
                    "from tmpAddDiscItem where userid="+objGlobal.getUserId(),objGlobal.getConnection());
            return true;
        }catch (Exception ex) {
            objGlobal.setErrorMessage("Connection error");
            return false;
        }
    }

    public int getSno(){
        int autoSn=0;
        try {
            String query = "select en=isnull(max(sn),0)+1 from AddDiscountDetail";
            Statement stmt = objGlobal.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()) {
                autoSn=Integer.parseInt(rs.getString("en").toString());
            }
        }catch (Exception ex) { }
        return autoSn;
    }

    public ArrayList<AddDiscountScanItems> getAddDiscountScanItems(){
        ArrayList<AddDiscountScanItems> listnewData=new ArrayList<AddDiscountScanItems>();
        String iCode="";
        float iRate=0,iDiscPer=0;
        try{
            String query = "select * from tmpAddDiscItem where userid="+objGlobal.getUserId();
            Statement stmt = objGlobal.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                iCode=rs.getString("itemcode").toString();
                iRate=rs.getFloat("Rate");
                iDiscPer=rs.getFloat("DiscPer");
                listnewData.add(new AddDiscountScanItems(iCode,iRate,iDiscPer));
            }
        }catch (Exception ex){ }
        return listnewData;
    }


}
