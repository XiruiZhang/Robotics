package ca.mcgill.ecse211.data;

public class LocalizationData
{
  //LocalizationData Attributes
  private int LLx;
  private int LLy;
  private int URx;
  private int URy;
  private int TB;
  private int SC;
  
  // empty constructor
  public LocalizationData() {
	  
  }
  // constructor overload
  public LocalizationData(int aLLx, int aLLy, int aURx, int aURy, int aTB, int aSC)
  {
    LLx = aLLx;
    LLy = aLLy;
    URx = aURx;
    URy = aURy;
    TB = aTB;
    SC = aSC;
  }
  
  public boolean setLLx(int aLLx)
  {
    boolean wasSet = false;
    LLx = aLLx;
    wasSet = true;
    return wasSet;
  }

  public boolean setLLy(int aLLy)
  {
    boolean wasSet = false;
    LLy = aLLy;
    wasSet = true;
    return wasSet;
  }

  public boolean setURx(int aURx)
  {
    boolean wasSet = false;
    URx = aURx;
    wasSet = true;
    return wasSet;
  }

  public boolean setURy(int aURy)
  {
    boolean wasSet = false;
    URy = aURy;
    wasSet = true;
    return wasSet;
  }

  public boolean setTB(int aTB)
  {
    boolean wasSet = false;
    TB = aTB;
    wasSet = true;
    return wasSet;
  }

  public boolean setSC(int aSC)
  {
    boolean wasSet = false;
    SC = aSC;
    wasSet = true;
    return wasSet;
  }

  public int getLLx()
  {
    return LLx;
  }

  public int getLLy()
  {
    return LLy;
  }

  public int getURx()
  {
    return URx;
  }

  public int getURy()
  {
    return URy;
  }

  public int getTB()
  {
    return TB;
  }

  public int getSC()
  {
    return SC;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "LLx" + ":" + getLLx()+ "," +
            "LLy" + ":" + getLLy()+ "," +
            "URx" + ":" + getURx()+ "," +
            "URy" + ":" + getURy()+ "," +
            "TB" + ":" + getTB()+ "," +
            "SC" + ":" + getSC()+ "]";
  }
}