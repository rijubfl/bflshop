package com.bflgroup.bflshop.ui.ginverification;

public class GinVerificationTicket {
    public String ginNo;
    public String shopName;
    public String palletNo;
    public String trfNo;
    public String toteId;
    public String verified;
    public String skuscan;


    public GinVerificationTicket(String ginNo, String shopName, String palletNo, String trfNo, String toteId, String verified, String skuscan) {
        this.ginNo = ginNo;
        this.shopName = shopName;
        this.palletNo = palletNo;
        this.trfNo = trfNo;
        this.toteId = toteId;
        this.verified = verified;
        this.skuscan = skuscan;
    }
}
