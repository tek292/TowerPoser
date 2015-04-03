package com.riis.towerpower.models;

/**
 * @author tkocikjr
 */
public class Tower
{
    private int mNetworkType;
    private float mAverageRSRPAsu;
    private float mAverageRSRPDb;
    private float mSampleSizeRSRP;
    private float mDownloadSpeed;
    private float mUploadSpeed;
    private float mPingTime;
    private float mReliability;
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

    public float getAverageRSRPAsu()
    {
        return mAverageRSRPAsu;
    }

    public void setAverageRSRPAsu(float averageRSRPAsu)
    {
        this.mAverageRSRPAsu = averageRSRPAsu;
    }

    public float getAverageRSRPDb()
    {
        return mAverageRSRPDb;
    }

    public void setAverageRSRPDb(float averageRSRPDb)
    {
        this.mAverageRSRPDb = averageRSRPDb;
    }

    public float getSampleSizeRSRP()
    {
        return mSampleSizeRSRP;
    }

    public void setSampleSizeRSRP(float sampleSizeRSRP)
    {
        this.mSampleSizeRSRP = sampleSizeRSRP;
    }

    public float getDownloadSpeed()
    {
        return mDownloadSpeed;
    }

    public void setDownloadSpeed(float downloadSpeed)
    {
        this.mDownloadSpeed = downloadSpeed;
    }

    public float getUploadSpeed()
    {
        return mUploadSpeed;
    }

    public void setUploadSpeed(float uploadSpeed)
    {
        this.mUploadSpeed = uploadSpeed;
    }

    public float getPingTime()
    {
        return mPingTime;
    }

    public void setPingTime(float pingTime)
    {
        this.mPingTime = pingTime;
    }

    public float getReliability()
    {
        return mReliability;
    }

    public void setReliability(float reliability)
    {
        this.mReliability = reliability;
    }
}
