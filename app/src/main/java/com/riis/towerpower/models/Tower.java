package com.riis.towerpower.models;

/**
 * @author tkocikjr
 */
public class Tower
{
    private int mNetworkType;
    private double mAverageRSSIAsu;
    private double mAverageRSSIDb;
    private double mSampleSizeRSSI;
    private double mDownloadSpeed;
    private double mUploadSpeed;
    private double mPingTime;
    private double mReliability;
    private String mNetworkName;

    public Tower(String name, int type)
    {
        mNetworkName = name;
        mNetworkType = type;
    }

    public String getNetworkName()
    {
        return mNetworkName;
    }

    public int getNetworkType()
    {
        return mNetworkType;
    }

    public double getAverageRSSIAsu()
    {
        return mAverageRSSIAsu;
    }

    public void setAverageRSSIAsu(double averageRSRPAsu)
    {
        this.mAverageRSSIAsu = averageRSRPAsu;
    }

    public double getAverageRSSIDb()
    {
        return mAverageRSSIDb;
    }

    public void setAverageRSSIDb(double averageRSRPDb)
    {
        this.mAverageRSSIDb = averageRSRPDb;
    }

    public double getSampleSizeRSSI()
    {
        return mSampleSizeRSSI;
    }

    public void setSampleSizeRSSI(double sampleSizeRSRP)
    {
        this.mSampleSizeRSSI = sampleSizeRSRP;
    }

    public double getDownloadSpeed()
    {
        return mDownloadSpeed;
    }

    public void setDownloadSpeed(double downloadSpeed)
    {
        this.mDownloadSpeed = downloadSpeed;
    }

    public double getUploadSpeed()
    {
        return mUploadSpeed;
    }

    public void setUploadSpeed(double uploadSpeed)
    {
        this.mUploadSpeed = uploadSpeed;
    }

    public double getPingTime()
    {
        return mPingTime;
    }

    public void setPingTime(double pingTime)
    {
        this.mPingTime = pingTime;
    }

    public double getReliability()
    {
        return mReliability;
    }

    public void setReliability(double reliability)
    {
        this.mReliability = reliability;
    }
}
