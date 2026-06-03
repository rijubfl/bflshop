package com.bflgroup.bflshop.ui.ageingslashing.reports;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.db.DBConnection;
import com.bflgroup.bflshop.ui.ageingslashing.AgeingSlashingGlobal;
import com.bflgroup.bflshop.ui.ageingslashing.model.AgeingSlashingItemsReports;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AgeingSlashingReportControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private AgeingSlashingGlobal objAgeingSlashingGlobal = AgeingSlashingGlobal.getInstance();

    private boolean b_Result;
    private ResultSet rs;

    public AgeingSlashingReportControl() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingSlashingReportControl : Local Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("AgeingSlashingReportControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }


    public List<String> loadBatchNo() {
        List<String> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<String>();
            rs = dbConnection.getResultSet("select distinct batchno from ageingitems order by 1 desc", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("batchno"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

    public List<String> loadType(String type, String batchno) {
        List<String> arr;
        if (!checkConnection()) {
            return null;
        }
        try {
            arr = new ArrayList<String>();
            if (type.equals("Group"))
                rs = dbConnection.getResultSet("select distinct vl=GroupName from ageingitems where batchno='" + batchno + "' order by 1", objGlobal.getConnection());
            if (type.equals("Department"))
                rs = dbConnection.getResultSet("select distinct vl=Department from ageingitems where batchno='" + batchno + "' order by 1", objGlobal.getConnection());
            if (type.equals("Division"))
                rs = dbConnection.getResultSet("select distinct vl=Division from ageingitems where batchno='" + batchno + "' order by 1", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("vl"));
            }
            return arr;
        } catch (Exception e) {
            objGlobal.setErrorMessage("" + e.toString());
            return null;
        }
    }

    ArrayList<AgeingSlashingItemsReports> loadReports(String batchNo,String fieldType,String search,String sortField,String sortType) {
        if (!checkConnection()) {
            return null;
        }
        ArrayList<AgeingSlashingItemsReports> listAgeingSlashingItemsReports = new ArrayList<AgeingSlashingItemsReports>();
        try {
            String sort="";
            if(sortType.equals("Z to A")) sort=" desc";
            if(sortField.equals("Eligible Qty")) sortField="ElgQty";
            if(sortField.equals("Scan Qty")) sortField="ScanQty";
            if(sortField.equals("Print Qty")) sortField="PrintQty";
            if(sortField.equals("Balance Qty")) sortField="Diff";
            listAgeingSlashingItemsReports.clear();
            b_Result = dbConnection.insertUpdate("create table #agreport(BatchNo varchar(10),Itemcode varchar(15),ElgQty int,ScanQty int,PrintQty int,Diff int)", objGlobal.getConnection());
            if (b_Result == false) {
                return null;
            }
            b_Result = dbConnection.insertUpdate("ALTER TABLE #agreport ALTER COLUMN itemcode VARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS", objGlobal.getConnection());
            if (b_Result == false) {
                return null;
            }
            b_Result = dbConnection.insertUpdate("insert into #agreport select batchno,itemcode,EligibleQty,0,0,0 from ageingitems where batchno='" + batchNo + "'", objGlobal.getConnection());
            if (b_Result == false) {
                return null;
            }
            b_Result = dbConnection.insertUpdate("insert into #agreport select batchno,itemcode,0,1,0,0 from scanageinglog where batchno='" + batchNo + "' and Remarks<>'REPRINT'", objGlobal.getConnection());
            if (b_Result == false) {
                return null;
            }
            b_Result = dbConnection.insertUpdate("insert into #agreport select batchno,itemcode,0,0,1,0 from ageingitemslognew where batchno='" + batchNo + "'", objGlobal.getConnection());
            if (b_Result == false) {
                return null;
            }
            b_Result = dbConnection.insertUpdate("alter table #agreport add GroupName varchar(150),Department varchar(150),Division varchar(150)", objGlobal.getConnection());
            if (b_Result == false) {
                return null;
            }
            b_Result = dbConnection.insertUpdate("update #agreport set GroupName=b.GroupName,Department=b.Department,Division=b.Division from #agreport a,ageingitems b where a.itemcode=b.itemcode", objGlobal.getConnection());
            if (b_Result == false) {
                return null;
            }
            b_Result = dbConnection.insertUpdate("update #agreport set GroupName='--Not Found--' where isnull(GroupName,'')=''", objGlobal.getConnection());
            if (b_Result == false) {
                return null;
            }
            b_Result = dbConnection.insertUpdate("update #agreport set Department='--Not Found--' where isnull(Department,'')=''", objGlobal.getConnection());
            if (b_Result == false) {
                return null;
            }
            b_Result = dbConnection.insertUpdate("update #agreport set Division='--Not Found--' where isnull(Division,'')=''", objGlobal.getConnection());
            if (b_Result == false) {
                return null;
            }
            rs = dbConnection.getResultSet("select batchno," + fieldType + ",ElgQty=sum(ElgQty),ScanQty=sum(ScanQty),PrintQty=sum(PrintQty),diff=sum(isnull(ElgQty,0))-sum(isnull(ScanQty,0)) from " +
                    "#agreport where " + fieldType + " like '%" + search + "%' group by batchno," + fieldType + " order by "+ sortField + sort, objGlobal.getConnection());
            while (rs.next()) {
                listAgeingSlashingItemsReports.add(new AgeingSlashingItemsReports(rs.getString(fieldType), rs.getInt("ElgQty"), rs.getInt("ScanQty"),
                        rs.getInt("PrintQty"), rs.getInt("diff")));
            }
            rs = dbConnection.getResultSet("select ElgQty=sum(ElgQty),ScanQty=sum(ScanQty),PrintQty=sum(PrintQty),diff=sum(isnull(ElgQty,0))-sum(isnull(ScanQty,0)) from #agreport", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingSlashingGlobal.setRptTotalElgQty(rs.getInt("ElgQty"));
                objAgeingSlashingGlobal.setRptTotalScanQty(rs.getInt("ScanQty"));
                objAgeingSlashingGlobal.setRptTotalPrintQty(rs.getInt("PrintQty"));
                objAgeingSlashingGlobal.setRptTotalDiff(rs.getInt("diff"));
            }
            b_Result = dbConnection.insertUpdate("drop table #agreport", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.setErrorMessage("AgeingSlashingControl:loadReports:" + "Slashing report not available");
                return null;
            }


        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingSlashingControl:loadReports:" + ex);
            return null;
        }
        return listAgeingSlashingItemsReports;
    }


}
