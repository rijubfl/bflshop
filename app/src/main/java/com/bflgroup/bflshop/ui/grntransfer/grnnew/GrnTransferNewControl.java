package com.bflgroup.bflshop.ui.grntransfer.grnnew;

import android.text.TextUtils;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.transform.Result;

public class GrnTransferNewControl {

    private DBConnection dbConnection = new DBConnection();

    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();

    private GrnTransferNewGlobal objGrnTransferNewGlobal = GrnTransferNewGlobal.getInstance();

    private boolean b_Result;
    private boolean firstGrn;
    private ResultSet rs;

    public GrnTransferNewControl() {
        checkConnection();
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("GrnTransferNewControl : Local Connection error");
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (!b_Result) {
            objGlobal.setErrorMessage("GrnTransferNewControl : Fetch Time error");
        }
        if (!objPosGlobal.getCloudMode().equals("Y")) {
            objGlobal.setCloudDbName("BFLDATA");
            b_Result = dbConnection.connectCloudDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("GrnShopTransferControl : Cloud Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validateTransferNumber(boolean view,boolean save, String scan, String ginno) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(ginno)) {
            objGlobal.setErrorMessage("Please enter GIN Number");
            return false;
        }
        if (TextUtils.isEmpty(scan)) {
            objGlobal.setErrorMessage("Please enter Transfer Number");
            return false;
        }
        try {
            if(objPosGlobal.getCloudMode().equals("Y")) {
                rs = dbConnection.getResultSet("select top 1 * from BFLDATA.dbo.GoodsIssue where sn='" + ginno + "' and ActualShop='" + objPosGlobal.getShopName() + "'", objGlobal.getCloudCon());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Invalid GIN Number, " + ginno);
                    return false;
                }
            }
            else {
                rs = dbConnection.getResultSet("select top 1 * from GoodsIssue where GINNo='" + ginno + "' and ShopName='" + objPosGlobal.getShopName() + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Invalid GIN Number, " + ginno);
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select top 1 TrfNo,TrfDate=convert(varchar,TrfDate,103),StoreIssue from transferheader where (trfno='" + scan + "' or StoreIssue='" + scan + "') order by trfdate desc", objGlobal.getConnection());
            if (rs.next()) {
                objGrnTransferNewGlobal.setTrfno(rs.getString("trfno"));
                objGrnTransferNewGlobal.setTrfdate(rs.getString("trfdate"));
                objGrnTransferNewGlobal.setToteid(rs.getString("storeissue"));
                objGrnTransferNewGlobal.setFromshop("HO");
            } else {
                objGlobal.setErrorMessage("Invalid Transfer Number/Toteid, " + scan);
                return false;
            }
            if(objPosGlobal.getCloudMode().equals("Y")) {
                rs = dbConnection.getResultSet("select top 1 * from BFLDATA.dbo.GoodsIssue where sn='" + ginno + "' and trfno='" + objGrnTransferNewGlobal.getTrfno() + "'", objGlobal.getCloudCon());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Transfer(" + objGrnTransferNewGlobal.getTrfno() + ") is not found in GIN(" + ginno + ")");
                    return false;
                }
            } else {
                rs = dbConnection.getResultSet("select top 1 * from GoodsIssue where GINNo='" + ginno + "' and trfno='" + objGrnTransferNewGlobal.getTrfno() + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Transfer(" + objGrnTransferNewGlobal.getTrfno() + ") is not found in GIN(" + ginno + ")");
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select * from grnheaderrf where trfno='" + objGrnTransferNewGlobal.getTrfno() + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (!view) {
                    objGlobal.setErrorMessage("Transfer already found in GRN, " + objGrnTransferNewGlobal.getTrfno());
                    return false;
                }
            } else {
                if (view) {
                    objGlobal.setErrorMessage("Transfer not found in GRN, " + objGrnTransferNewGlobal.getTrfno());
                    return false;
                }
            }
            if (!save) {
                b_Result = dbConnection.insertUpdate("delete from tmpGrnScanItems where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                if (!b_Result) {
                    return false;
                }
                b_Result = dbConnection.insertUpdate("insert into tmpGrnScanItems(DeviceId,Itemcode,Description,Price,TrfQty,ScanQty,ScanPrice,SysStock,Rfid,SerializedCode,Scan) " +
                        "select '" + objGlobal.getDeviceName() + "',itemcode,(select description from itemmaster where ItemCode=a.itemcode),(select top 1 salesrate from SalesPrice " +
                        "where CostCode='" + objPosGlobal.getCostCode() + "' and ItemCode=a.itemcode order by trndate desc),Quantity,0,0,(select sum(quantity) from locstock where " +
                        "CostCode='" + objPosGlobal.getCostCode() + "' and ItemCode=a.itemcode),EPC,SerializedCode,'N' from TransferDetail a where TrfNo='" + objGrnTransferNewGlobal.getTrfno() + "'", objGlobal.getConnection());
                if (!b_Result) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:validateTransferNumber:" + ex.getMessage());
            return false;
        }
    }
    private boolean getLatestGrn(String formattedDate) {
        String grfNo = "";
        int autoSn = 0;
        try {
            String query = "", prefix = "1";
            String yr = formattedDate.substring(formattedDate.lastIndexOf("/") + 1);
            firstGrn=true;
            prefix = objPosGlobal.getShopLetter() + yr.substring(2, 4);
            rs = dbConnection.getResultSet("select entryno from grnheader where entrydate='" + formattedDate + "'", objGlobal.getConnection());
            if (rs.next()) {
                grfNo = rs.getString("entryno").toString();
                firstGrn=false;
            } else {
                if (objPosGlobal.getShopLetter().length() == 2) {
                    query = "select en=isnull(max(right(entryno, len(entryno)-4)),0)+1 from grnheader where entryno like '" + prefix + "%'";
                } else {
                    query = "select en=isnull(max(right(entryno, len(entryno)-3)),0)+1 from grnheader where entryno like '" + prefix + "%'";
                }
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    autoSn = Integer.parseInt(rs.getString("en").toString());
                }
                grfNo = prefix + String.format("%05d", autoSn);
            }
            objGrnTransferNewGlobal.setLatestGrnNo(grfNo);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:getLatestGrn:" + ex.getMessage());
            return false;
        }
    }

    private boolean getLatestGrnRf() {
        String grfNo = "";
        int autoSn = 0;
        try {
            String query = "", prefix = "";
            String yr = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            prefix = objPosGlobal.getShopLetter() + yr.substring(2, 4);
            if (objPosGlobal.getShopLetter().length() == 2) {
                query = "select en=isnull(max(right(entryno, len(entryno)-4)),0)+1 from grnheaderrf where entryno like '" + prefix + "%'";
            } else {
                query = "select en=isnull(max(right(entryno, len(entryno)-3)),0)+1 from grnheaderrf where entryno like '" + prefix + "%'";
            }
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            if (rs.next()) {
                autoSn = Integer.parseInt(rs.getString("en").toString());
            }
            grfNo = prefix + String.format("%05d", autoSn);
            objGrnTransferNewGlobal.setLatestGrnNoRf(grfNo);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:getLatestGrnRf:" + ex.getMessage());
            return false;
        }
    }

    public boolean validateRfid(String scan,String trfno) {
        boolean found = false;
        try {
            objGrnTransferNewGlobal.setScanBarcode("");
            objGrnTransferNewGlobal.setScanRFID("");
            objGrnTransferNewGlobal.setScanSeriali("");
            rs = dbConnection.getResultSet("select Itemcode from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and scan='Y' and RFID='" + scan + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("RFID already scan, (" + scan + ", Itemcode:" + rs.getString("Itemcode") + ")");
                return false;
            }
            rs = dbConnection.getResultSet("select Itemcode from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and scan='Y' and SerializedCode='" + scan + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Serialized Code already scan, (" + scan + ", Itemcode:" + rs.getString("Itemcode") + ")");
                return false;
            }
            rs = dbConnection.getResultSet("select Itemcode,RFID=isnull(RFID,''),SerializedCode=isnull(SerializedCode,'') from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and SerializedCode='" + scan + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGrnTransferNewGlobal.setScanBarcode(rs.getString("Itemcode"));
                objGrnTransferNewGlobal.setScanRFID(rs.getString("RFID"));
                objGrnTransferNewGlobal.setScanSeriali(rs.getString("SerializedCode"));
                found = true;
            }
            if (!found) {
                rs = dbConnection.getResultSet("select Itemcode,RFID=isnull(RFID,''),SerializedCode=isnull(SerializedCode,'') from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and RFID='" + scan + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGrnTransferNewGlobal.setScanBarcode(rs.getString("Itemcode"));
                    objGrnTransferNewGlobal.setScanRFID(rs.getString("RFID"));
                    objGrnTransferNewGlobal.setScanSeriali(rs.getString("SerializedCode"));
                    found = true;
                }
            }
            if (!found) {
                rs = dbConnection.getResultSet("select barcode,RFID=isnull(RFID,''),SerializedCode=isnull(SerializedCode,'') from RFPair where SerializedCode='" + scan + "' and TrfNo='" + trfno + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGrnTransferNewGlobal.setScanBarcode(rs.getString("barcode"));
                    objGrnTransferNewGlobal.setScanRFID(rs.getString("RFID"));
                    objGrnTransferNewGlobal.setScanSeriali(rs.getString("SerializedCode"));
                    found = true;
                }
            }
            if (!found) {
                rs = dbConnection.getResultSet("select barcode,RFID,SerializedCode from RFPair where RFID='" + scan + "' and TrfNo='" + trfno + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGrnTransferNewGlobal.setScanBarcode(rs.getString("barcode"));
                    objGrnTransferNewGlobal.setScanRFID(rs.getString("RFID"));
                    objGrnTransferNewGlobal.setScanSeriali(rs.getString("SerializedCode"));
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:validateRfid:" + ex.getMessage());
            return false;
        }
    }

    public boolean validateScanItem(String itemcode,int qty, String rfid,String serializedCode, float scanprice) {
        boolean itemfound = false;
        String description = "";
        float price = 0;
        int trfQty = 0, scanQty = 0, diffQty = 0;
        int sysstock = 0;
        objGrnTransferNewGlobal.setScanDescription("");
        objGrnTransferNewGlobal.setScanSysStock(0);
        objGrnTransferNewGlobal.setScanSysPrice(0);
        try {
            rs = dbConnection.getResultSet("select Itemcode,description,Price,ScanPrice,SysStock from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                description = rs.getString("description");
                price = rs.getFloat("Price");
                sysstock = rs.getInt("SysStock");
                itemfound = true;
            }
            if (!itemfound) {
                rs = dbConnection.getResultSet("select description,sprice=(select salesrate from SalesPrice where CostCode='" + objPosGlobal.getCostCode() + "' and ItemCode=a.itemcode)," +
                        "stock=(select quantity from locstock where itemcode=a.itemcode and costcode='" + objPosGlobal.getCostCode() + "') from ItemMaster a where ItemCode='" + itemcode + "'", objGlobal.getConnection());
                if (rs.next()) {
                    description = rs.getString("description");
                    price = rs.getFloat("sprice");
                    sysstock = rs.getInt("stock");
                }
                if (objPosGlobal.getGrnItemVal().equals("Y")) {
                    if (description.isEmpty()) {
                        objGlobal.setErrorMessage("Can't proceed, Invalid item or description is blank");
                        return false;
                    }
                }
                if (objPosGlobal.getGrnSpVal().equals("Y")) {
                    if (price == 0) {
                        objGlobal.setErrorMessage("Can't proceed, Sales Price is 0");
                        return false;
                    }
                }
                b_Result = dbConnection.insertUpdate("insert into tmpGrnScanItems(DeviceId,Itemcode,Description,Price,TrfQty,ScanQty,ScanPrice,SysStock,rfid,serializedCode) values ('" + objGlobal.getDeviceName() + "'," +
                        "'" + itemcode + "','" + description + "'," + price + ",0,0," + scanprice + "," + sysstock + ",'" + rfid + "','" + serializedCode + "')", objGlobal.getConnection());
                if (!b_Result) {
                    return false;
                }
            }
            if(!serializedCode.isEmpty()) {
                b_Result = dbConnection.insertUpdate("update tmpGrnScanItems set Scan='Y',ScanPrice=" + scanprice + ",ScanQty=ScanQty+" + qty + ",ScanDt=convert(varchar,getdate(),103)," +
                        "ScanTm=convert(varchar,getdate(),8) where DeviceId='" + objGlobal.getDeviceName() + "' and serializedCode='" + serializedCode + "'", objGlobal.getConnection());
                if (!b_Result) {
                    return false;
                }
            } else {
                if (!rfid.isEmpty()) {
                    b_Result = dbConnection.insertUpdate("update tmpGrnScanItems set Scan='Y',ScanPrice=" + scanprice + ",ScanQty=ScanQty+" + qty + ",ScanDt=convert(varchar,getdate(),103),ScanTm=convert(varchar,getdate(),8) " +
                            "where DeviceId='" + objGlobal.getDeviceName() + "' and rfid='" + rfid + "'", objGlobal.getConnection());
                    if (!b_Result) {
                        return false;
                    }
                } else {
                    if(!itemcode.isEmpty()) {
                        ResultSet rsCheck = dbConnection.getResultSet("select * from tmpgrnscanitems where DeviceId='" + objGlobal.getDeviceName() + "' and itemcode='" + itemcode + "' and scanqty < trfqty",objGlobal.getConnection());
                        if (rsCheck.next()) {
                            b_Result = dbConnection.insertUpdate("update top (1) tmpGrnScanItems set Scan='Y',ScanPrice=" + scanprice + ",ScanQty=ScanQty+" + qty + "," +
                                            "ScanDt=convert(varchar,getdate(),103),ScanTm=convert(varchar,getdate(),8) where DeviceId='" + objGlobal.getDeviceName() + "' and itemcode='" + itemcode + "' and scanqty < trfqty",
                                    objGlobal.getConnection());
                        }
                        else{
                            b_Result = dbConnection.insertUpdate("update top (1) tmpGrnScanItems set Scan='Y',ScanPrice=" + scanprice + ",ScanQty=ScanQty+" + qty + "," +
                                            "ScanDt=convert(varchar,getdate(),103),ScanTm=convert(varchar,getdate(),8) where DeviceId='" + objGlobal.getDeviceName() + "' and itemcode='" + itemcode + "'",
                                    objGlobal.getConnection());
                        }
                        if (!b_Result) {
                            return false;
                        }
                    }
                }
            }

            rs = dbConnection.getResultSet("select TrfQty=sum(TrfQty),ScanQty=sum(ScanQty),DiffQty=sum(ScanQty)-sum(TrfQty) from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                trfQty = rs.getInt("TrfQty");
                scanQty = rs.getInt("ScanQty");
                diffQty = rs.getInt("DiffQty");
            }
            objGrnTransferNewGlobal.setScanDescription(description);
            objGrnTransferNewGlobal.setScanSysStock(sysstock);
            objGrnTransferNewGlobal.setScanSysPrice(price);
            objGrnTransferNewGlobal.setItemscanqty(scanQty);
            objGrnTransferNewGlobal.setItemtrfqty(trfQty);
            objGrnTransferNewGlobal.setItemdiffqty(diffQty);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:validateScanItem:" + ex.getMessage());
            return false;
        }
    }

    public boolean editItem(String itemcode,int qty) {
        try {
            int totalScanQty = 0;

            ResultSet rsCheck = dbConnection.getResultSet(
                    "SELECT ISNULL(SUM(ScanQty), 0) AS totalscanqty " +
                            "FROM tmpGrnScanItems " +
                            "WHERE DeviceId='" + objGlobal.getDeviceName() + "' " +
                            "AND Itemcode='" + itemcode + "'",
                    objGlobal.getConnection()
            );

            if (rsCheck.next()) {
                totalScanQty = rsCheck.getInt("totalscanqty");
            }
            rsCheck.close();

            if (qty > totalScanQty) {

                int addQty = qty - totalScanQty;

                // Step 1: Fill rows where ScanQty < TrfQty
                while (addQty > 0) {

                    ResultSet rsItem = dbConnection.getResultSet(
                            "SELECT TOP (1) " +
                                    "ISNULL(ScanQty,0) AS ScanQty, " +
                                    "ISNULL(TrfQty,0) AS TrfQty " +
                                    "FROM tmpGrnScanItems " +
                                    "WHERE DeviceId='" + objGlobal.getDeviceName() + "' " +
                                    "AND Itemcode='" + itemcode + "' " +
                                    "AND ISNULL(ScanQty,0) < ISNULL(TrfQty,0) " +
                                    "ORDER BY ScanQty ASC",
                            objGlobal.getConnection()
                    );

                    if (!rsItem.next()) {
                        rsItem.close();
                        break;
                    }

                    int currentScanQty = rsItem.getInt("ScanQty");
                    int trfQty = rsItem.getInt("TrfQty");

                    rsItem.close();

                    int availableQty = trfQty - currentScanQty;
                    int updateQty = Math.min(availableQty, addQty);

                    b_Result = dbConnection.insertUpdate(
                            "UPDATE TOP (1) tmpGrnScanItems " +
                                    "SET ScanQty=ISNULL(ScanQty,0)+" + updateQty + ", " +
                                    "ScanDt=CONVERT(VARCHAR,GETDATE(),103), " +
                                    "ScanTm=CONVERT(VARCHAR,GETDATE(),8) " +
                                    "WHERE DeviceId='" + objGlobal.getDeviceName() + "' " +
                                    "AND Itemcode='" + itemcode + "' " +
                                    "AND ISNULL(ScanQty,0)=" + currentScanQty + " " +
                                    "AND ISNULL(TrfQty,0)=" + trfQty + " " +
                                    "AND ISNULL(ScanQty,0)<ISNULL(TrfQty,0)",
                            objGlobal.getConnection()
                    );

                    if (!b_Result) {
                        break;
                    }

                    addQty -= updateQty;
                }

                // Step 2: Add extra quantity only after all rows are filled
                if (b_Result && addQty > 0) {

                    b_Result = dbConnection.insertUpdate(
                            "UPDATE TOP (1) tmpGrnScanItems " +
                                    "SET ScanQty=ISNULL(ScanQty,0)+" + addQty + ", " +
                                    "ScanDt=CONVERT(VARCHAR,GETDATE(),103), " +
                                    "ScanTm=CONVERT(VARCHAR,GETDATE(),8) " +
                                    "WHERE DeviceId='" + objGlobal.getDeviceName() + "' " +
                                    "AND Itemcode='" + itemcode + "'",
                            objGlobal.getConnection()
                    );
                }
            } else if (qty < totalScanQty) {

                // Reduce scanned quantity
                int removeQty = totalScanQty - qty;

                while (removeQty > 0) {

                    // Get one row with positive ScanQty
                    ResultSet rsItem = dbConnection.getResultSet(
                            "SELECT TOP (1) ScanQty " +
                                    "FROM tmpGrnScanItems " +
                                    "WHERE DeviceId='" + objGlobal.getDeviceName() + "' " +
                                    "AND Itemcode='" + itemcode + "' " +
                                    "AND ScanQty > 0 " +
                                    "ORDER BY ScanQty DESC",
                            objGlobal.getConnection()
                    );

                    if (!rsItem.next()) {
                        rsItem.close();
                        b_Result = false;
                        break;
                    }

                    int currentQty = rsItem.getInt("ScanQty");
                    rsItem.close();

                    int deductQty = Math.min(currentQty, removeQty);

                    b_Result = dbConnection.insertUpdate(
                            "UPDATE TOP (1) tmpGrnScanItems " +
                                    "SET ScanQty=ScanQty-" + deductQty + ", " +
                                    "ScanDt=CONVERT(VARCHAR,GETDATE(),103), " +
                                    "ScanTm=CONVERT(VARCHAR,GETDATE(),8) " +
                                    "WHERE DeviceId='" + objGlobal.getDeviceName() + "' " +
                                    "AND Itemcode='" + itemcode + "' " +
                                    "AND ScanQty=" + currentQty,
                            objGlobal.getConnection()
                    );

                    if (!b_Result) {
                        break;
                    }

                    removeQty -= deductQty;
                }

            } else {

                // Already matching
                b_Result = true;
            }
            return b_Result;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:editItem:" + ex.getMessage());
            return false;
        }
    }

    public boolean viewExistItem(String itemcode) {
        String description = "";
        float price = 0;
        int trfQty=0,scanQty=0,diffQty=0;
        objGrnTransferNewGlobal.setScanDescription("");
        //objGrnTransferNewGlobal.setScanPrice(0);
        try {
            String query = "SELECT Itemcode, description, Price, " +
                    "MAX(TrfQty) AS TrfQty, " +
                    "SUM(ISNULL(ScanQty, 0)) AS ScanQty, " +
                    "SUM(ISNULL(ScanQty, 0)) - MAX(TrfQty) AS DiffQty " +
                    "FROM tmpGrnScanItems " +
                    "WHERE DeviceId='" + objGlobal.getDeviceName() + "' " +
                    "AND Itemcode='" + itemcode + "' " +
                    "GROUP BY Itemcode, description, Price";

            rs = dbConnection.getResultSet(query, objGlobal.getConnection());

            if (rs.next()) {
                description = rs.getString("description");
                price = rs.getFloat("Price");
                trfQty = rs.getInt("TrfQty");
                scanQty = rs.getInt("ScanQty");
                diffQty = rs.getInt("DiffQty");
            }
            objGrnTransferNewGlobal.setScanDescription(description);
            //objGrnTransferNewGlobal.setScanPrice(price);
            objGrnTransferNewGlobal.setItemscanqty(scanQty);
            objGrnTransferNewGlobal.setItemtrfqty(trfQty);
            objGrnTransferNewGlobal.setItemdiffqty(diffQty);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:validateScanItem:" + ex.getMessage());
            return false;
        }
    }
    public boolean deleteAll() {
        try {
            b_Result = dbConnection.insertUpdate("delete from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:delete:" + ex.getMessage());
            return false;
        }
    }

    public boolean findExcessAndMissing() {
        int excess = 0, missing = 0;
        try {
            rs = dbConnection.getResultSet("select excess=sum(ScanQty-TrfQty) from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and TrfQty-ScanQty<0", objGlobal.getConnection());
            if (rs.next()) {
                excess = rs.getInt("excess");
            }
            rs = dbConnection.getResultSet("select missing=abs(sum(ScanQty-TrfQty)) from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and TrfQty-ScanQty>0", objGlobal.getConnection());
            if (rs.next()) {
                missing = rs.getInt("missing");
            }
            objGrnTransferNewGlobal.setTotalExcess(excess);
            objGrnTransferNewGlobal.setTotalMissing(missing);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:findExcessAndMissing:" + ex.getMessage());
            return false;
        }
    }

    public String validateManagerVerify(String password) {
        String retVal="";
        if(password.isEmpty()){
            objGlobal.setErrorMessage("Please enter password");
            return retVal;
        }
        try {
            rs = dbConnection.getResultSet("select mgrname from managercode where pwd='" + password + "' and isnull(mgrname,'')<>''", objGlobal.getConnection());
            if (rs.next()) {
                retVal = rs.getString("mgrname");
            } else {
                objGlobal.setErrorMessage("Invalid password");
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:validateTransferNumber:" + ex);
        }
        return retVal;
    }

    public ArrayList<GrnTransferNewTrfScanItems> loadGrnTrfScanItems() {
        ArrayList<GrnTransferNewTrfScanItems> listGrnTransferNewTrfScanItems = new ArrayList<GrnTransferNewTrfScanItems>();
        int scanqty = 0, trfqty = 0, diffqty = 0;
        objGrnTransferNewGlobal.setDiffqty(0);
        objGrnTransferNewGlobal.setTrfqty(0);
        objGrnTransferNewGlobal.setScanqty(0);
        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferNewControl:loadGrnTrfScanItems: connection error");
            return null;
        }
        try {
            rs = dbConnection.getResultSet("select itemcode,scanqty=sum(scanqty),trfqty=sum(trfqty),diffqty=sum(scanqty)-sum(trfqty) from tmpGrnScanItems where deviceid='" + objGlobal.getDeviceName() + "' group by itemcode order by itemcode", objGlobal.getConnection());
            while (rs.next()) {
                listGrnTransferNewTrfScanItems.add(new GrnTransferNewTrfScanItems(rs.getString("itemcode"), rs.getInt("scanqty"), rs.getInt("trfqty"), rs.getInt("diffqty")));
                scanqty = scanqty + rs.getInt("scanqty");
                trfqty = trfqty + rs.getInt("trfqty");
            }
            diffqty = scanqty - trfqty;
            objGrnTransferNewGlobal.setDiffqty(diffqty);
            objGrnTransferNewGlobal.setTrfqty(trfqty);
            objGrnTransferNewGlobal.setScanqty(scanqty);
            return listGrnTransferNewTrfScanItems;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:loadGrnTrfScanItems:" + ex.getMessage());
            return null;
        }
    }

    // Uses a row-level lock on TransferHeader rather than a global application lock.
    // Different transfers can be saved independently; the same transfer is serialized
    // by this method. Other save paths must follow the same rule or use unique indexes.
    public boolean grnSave(String trfNo, String trfDate, String ginno) {
        if (!validateTransferNumber(false, true, trfNo, ginno)) return false;
        final String actualTrfNo = objGrnTransferNewGlobal.getTrfno();
        if (TextUtils.isEmpty(actualTrfNo)) {
            objGlobal.setErrorMessage("Unable to resolve actual transfer number");
            return false;
        }
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) return false;

        java.sql.Connection connection = objGlobal.getConnection();
        boolean transactionStarted = false;
        try {
            connection.setAutoCommit(false);
            transactionStarted = true;

            // Lock the canonical transfer row until commit/rollback. This is not
            // sp_getapplock and does not serialize unrelated transfers.
            try (java.sql.PreparedStatement stmt = connection.prepareStatement(
                    "SELECT TrfNo FROM TransferHeader WITH (UPDLOCK, HOLDLOCK) WHERE TrfNo=?")) {
                stmt.setString(1, actualTrfNo);
                try (ResultSet result = stmt.executeQuery()) {
                    if (!result.next()) {
                        objGlobal.setErrorMessage("Transfer not found: " + actualTrfNo);
                        return rollbackGrn(connection);
                    }
                }
            }

            // Final checks must happen after acquiring the transfer lock.
            String existingSql = "SELECT 1 FROM GRNHeaderRF WHERE TrfNo=? " +
                    "UNION ALL SELECT 1 FROM GRNDetail WHERE TrfNo=?";
            try (java.sql.PreparedStatement stmt = connection.prepareStatement(existingSql)) {
                stmt.setString(1, actualTrfNo);
                stmt.setString(2, actualTrfNo);
                try (ResultSet result = stmt.executeQuery()) {
                    if (result.next()) {
                        objGlobal.setErrorMessage("Transfer already found in GRN: " + actualTrfNo);
                        return rollbackGrn(connection);
                    }
                }
            }

            try (java.sql.PreparedStatement stmt = connection.prepareStatement(
                    "SELECT TOP 1 1 FROM tmpGrnScanItems WHERE DeviceId=? AND ScanQty>0")) {
                stmt.setString(1, objGlobal.getDeviceName());
                try (ResultSet result = stmt.executeQuery()) {
                    if (!result.next()) {
                        objGlobal.setErrorMessage("No item is scanned for save");
                        return rollbackGrn(connection);
                    }
                }
            }

            // These methods use the same connection; neither may reconnect here.
            if (!getLatestGrnRf() || !getLatestGrn(objGlobal.getServerDate())) {
                return rollbackGrn(connection);
            }

            String rfNo = objGrnTransferNewGlobal.getLatestGrnNoRf();
            String grnNo = objGrnTransferNewGlobal.getLatestGrnNo();
            String date = objGlobal.getServerDate();
            String device = objGlobal.getDeviceName();

            if (!dbConnection.insertUpdate(
                    "insert into GRNHeaderRF(EntryNo,EntryDate,UserId,TrfNo) values('" +
                            rfNo + "','" + date + "','" + objGlobal.getUserName() + "','" + actualTrfNo + "')", connection)) {
                return rollbackGrn(connection);
            }
            if (!dbConnection.insertUpdate(
                    "insert into GRNdetailRF(EntryNo,GINNo,TrfNo,Itemcode,RfId,ScanMode,TrfQty,ScanQty,Diff,SerializedCode) " +
                            "select '" + rfNo + "','" + ginno + "','" + actualTrfNo +
                            "',itemcode,rfid,'',TrfQty,ScanQty,(ScanQty-TrfQty),SerializedCode " +
                            "from tmpGrnScanItems where deviceid='" + device + "'", connection)) {
                return rollbackGrn(connection);
            }
            if (firstGrn && !dbConnection.insertUpdate(
                    "insert into GRNHeader(EntryNo,EntryDate,Remarks,UserId) values('" +
                            grnNo + "','" + date + "',''," + objGlobal.getUserId() + ")", connection)) {
                return rollbackGrn(connection);
            }
            if (!dbConnection.insertUpdate(
                    "insert into GRNdetail(EntryNo,TrfNo,TrfDate,Qty,Remarks,ScanQty,Diff) " +
                            "select '" + grnNo + "','" + actualTrfNo + "','" + trfDate +
                            "',sum(TrfQty),'',sum(ScanQty),sum(ScanQty-TrfQty) " +
                            "from tmpGrnScanItems where deviceid='" + device + "'", connection)) {
                return rollbackGrn(connection);
            }
            connection.commit();
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("GrnTransferNewControl:grnSave: " + e.toString());
            if (transactionStarted) {
                try { connection.rollback(); } catch (SQLException rollbackError) {
                    objGlobal.setErrorMessage(objGlobal.getErrorMessage() + "; rollback: " + rollbackError);
                }
            }
            return false;
        } finally {
            if (transactionStarted) {
                try { connection.setAutoCommit(true); } catch (SQLException e) {
                    objGlobal.setErrorMessage("GRN save connection reset failed: " + e);
                }
            }
        }
    }

    private boolean rollbackGrn(java.sql.Connection connection) throws SQLException {
        connection.rollback();
        return false;
    }
}
