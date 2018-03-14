package subdiv.transvision.com.billing;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

import subdiv.transvision.com.billing.database.Databasehelper;
import subdiv.transvision.com.billing.values.FunctionCalls;

public class ClassCalculation {
    String prvRead1 = "", curReading1 = "", santionHp1 = "", sanctionKw1 = "", Readingconsume = "";
    String statusLabel1 = "", flagRebate = "", tariff = "", Rrebate = "", frate = "", bmdvalue = "", NoofWeeks = "", loadfrate = "";
    Cursor c = null;
    double pfFlagValue = 0, capFlagValue = 0, HP = 0;
    double cal_MF = 0, tod_ofPeakValue = 0, tod_onPeakValue = 0;
    Context con;
    double FC = 0, EC = 0, EC1 = 0, bill_Amount = 0, bmdPenalty = 0, finalPfPenalitiy = 0;
    double solarRebate = 0, handiRebate = 0, finalRebate = 0, CharityRate = 0, pfValue = 0, gok_payment = 0, cal_deposit = 0,
            intTaxPer = 0, dblintrPer = 0, dblTax = 0, dblIntrTax = 0, finalTODValue = 0, int_calArrears = 0,
            KW = 0, data1 = 0, data2 = 0, sanctionLoad = 0, fixedrate = 0, ccdl_count = 0, frate_2 = 0, intEslab = 0;
    double[] arrFrate = new double[10];
    double[] arrFslab = new double[10];
    double[] arrdlFslab = new double[10];
    double[] arrErate = new double[10];
    double[] arrEslab = new double[10];
    double[] arrEC = new double[10]; // Array of Ec of each Slab
    double[] arrFC = new double[10]; // Array of Fc of each Slab
    double[] arrE1rate = new double[10];
    double[] arrE1slab = new double[10];
    double[] arrEC1 = new double[10];
    Databasehelper dbh = new Databasehelper(con);
    FunctionCalls fcall = new FunctionCalls();
    Bitmap bmp;
    boolean additionalgok = false;

    // ====================== Setters =========================================

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public void setweeks(String noofweeks) {
        this.NoofWeeks = noofweeks;
    }

    public void setCalMF(int cal_MF) {
        this.cal_MF = cal_MF;
    }

    public double getPfValue() {
        return pfValue;
    }

    public void setPfValue(double pfValue) {
        this.pfValue = pfValue;
    }

    static double remKW, remUnits, Units, remconsume;

    public void setPrvRead(String prvRead) {
        this.prvRead1 = prvRead;
    }

    public void setCurReading(String curReading) {
        this.curReading1 = curReading;
    }

    public void setconsumtion(String consume) {
        this.Readingconsume = consume;
    }

    public void setSantionHp(String santionHp) {
        this.santionHp1 = santionHp;
    }

    public void setSanctionKw(String sanctionKw) {
        this.sanctionKw1 = sanctionKw;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel1 = statusLabel;
    }

    public void setCal_deposit(double cal_deposit) {
        this.cal_deposit = cal_deposit;
    }

    public void setFlagRebate(String flagRebate) {
        this.flagRebate = flagRebate;
    }

    public void setPfFlagValue(int pfFlagValue) {
        this.pfFlagValue = pfFlagValue;
    }

    public void setCapFlagValue(int capFlagValue) {
        this.capFlagValue = capFlagValue;
    }

    public void setTod_ofPeakValue(int tod_ofPeakValue) {
        this.tod_ofPeakValue = tod_ofPeakValue;
    }

    public void setTod_onPeakValue(int tod_onPeakValue) {
        this.tod_onPeakValue = tod_onPeakValue;
    }

    public void setInt_calArrears(double int_calArrears) {
        this.int_calArrears = int_calArrears;
    }

    public void setdl_count(double dl_count) {
        this.ccdl_count = dl_count;
    }

    public void settariff(String tariff) {
        this.tariff = tariff;
    }

    public void setRrebate(String rrebate) {
        this.Rrebate = rrebate;
    }

    public void setbmdvalue(String bmdvalue) {
        this.bmdvalue = bmdvalue;
    }

    public void setdata1(double data1) {
        this.data1 = data1;
    }

    public void setdata2(double data2) {
        this.data2 = data2;
    }

    public void setIntr_Amt(double Intr_Amt) {
        this.dblIntrTax = Intr_Amt;
    }

    // Function to Calculate Fixed
    // Charges=======================================================
    public ClassCalculation(Context context) {
        this.con = context;
    }

    public void FcCalculation(Cursor c, String dbtvalue) {
        Double testdouble = Double.parseDouble(sanctionKw1);
        String testString = testdouble.toString();
        String test1 = testString.substring(0, testString.lastIndexOf('.'));
        String test2 = testString.substring(testString.lastIndexOf('.') + 1, testString.length());
        Double test3 = Double.parseDouble(test2);
        if (StringUtils.startsWithIgnoreCase(dbtvalue, "4"))
            KW = testdouble;
        else {
            if (test3 > 0)
                KW = Double.parseDouble(test1) + 1;
            else KW = Double.parseDouble(test1);
        }
        if (!tariff.equals("70")) {
            HP = Double.parseDouble(santionHp1);
            if (dbtvalue.equals("4")) {
                if (tariff.equals("50") || tariff.equals("51") || tariff.equals("52") || tariff.equals("53")) {
                } else if (HP > 0) {
                    KW = HP;
                }
            } else if (tariff.equals("40") || tariff.equals("41") || tariff.equals("42") || tariff.equals("43") || tariff.equals("50")
                    || tariff.equals("51") || tariff.equals("52") || tariff.equals("53") || tariff.equals("60")) {
                if (HP > 0) {
                    String santHp = "" + HP;
                    String santHp1 = santHp.substring(0, santHp.lastIndexOf('.'));
                    String santHp2 = santHp.substring(santHp.lastIndexOf('.') + 1);
                    int santHp3 = 0;
                    if (santHp2.length() == 1) {
                        santHp3 = Integer.parseInt(santHp2 + "0");
                    } else santHp3 = Integer.parseInt(santHp2);
                    if (santHp3 >= 0 && santHp3 <= 25) {
                        KW = Double.parseDouble(santHp1 + "." + "00");
                    } else if (santHp3 > 25 && santHp3 <= 50) {
                        KW = Double.parseDouble(santHp1 + "." + "50");
                    } else if (santHp3 > 50 && santHp3 <= 75) {
                        KW = Double.parseDouble(santHp1 + "." + "50");
                    } else if (santHp3 > 75) {
                        KW = Double.parseDouble(santHp1 + "." + "00") + 1;
                    }
                }
            } else if (HP > 0) {
                KW = HP;
            }
            remKW = 0;
            fcall.logStatus("Reading Cursor");
            fcall.logStatus("Cursor Count: " + c.getCount());
            if (c.getCount() > 0) {
                fcall.logStatus("Cursor Count: " + c.getCount());
                c.moveToNext();
                fcall.logStatus("Cursor Count Column: " + c.getColumnCount());
                fcall.logStatus("FSLABS: " + c.getString(c.getColumnIndex("NOF_FSLABS")));
                String noOfSlab = c.getString(c.getColumnIndex("NOF_FSLABS"));
                int intNoOfSlab = Integer.parseInt(noOfSlab);
                loadfrate = c.getString(c.getColumnIndex("FRATE2"));
                for (int i = 1; i <= intNoOfSlab; i++) {
                    if (!tariff.equals("21")) {
                        String fslab = c.getString(c.getColumnIndex("FSLAB" + i));
                        int intFslab = Integer.parseInt(fslab);
                        String frate = c.getString(c.getColumnIndex("FRATE" + i));
                        double intFrate = Double.parseDouble(frate);
                        if (i == 1) {
                            if (KW <= intFslab) {
                                double ssd = ccdl_count;
                                arrFC[i] = ((ccdl_count + 1) * KW) * intFrate;
                                arrFrate[i] = intFrate;
                                arrFslab[i] = ((ccdl_count + 1) * KW);
                                arrdlFslab[i] = KW;
                                i = 11;
                            } else if (KW > intFslab) {
                                arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                remKW = KW - intFslab;
                                arrFrate[i] = intFrate;
                                arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                arrdlFslab[i] = intFslab;
                            }
                        }

                        if (i == 2) {
                            if (remKW <= intFslab) {
                                arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                arrFrate[i] = intFrate;
                                frate_2 = intFrate;
                                arrFslab[i] = ((ccdl_count + 1) * remKW);
                                arrdlFslab[i] = remKW;
                                i = 11;

                            } else if (remKW > intFslab) {
                                arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                remKW = remKW - intFslab;
                                arrFrate[i] = intFrate;
                                frate_2 = intFrate;
                                arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                arrdlFslab[i] = intFslab;
                            }
                        }

                        if (i == 3) {
                            if (remKW <= intFslab) {
                                arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                arrFrate[i] = intFrate;
                                arrFslab[i] = ((ccdl_count + 1) * remKW);
                                arrdlFslab[i] = remKW;
                                i = 11;

                            } else if (remKW > intFslab) {
                                arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                remKW = remKW - intFslab;
                                arrFrate[i] = intFrate;
                                arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                arrdlFslab[i] = intFslab;
                            }
                        }

                        if (i == 4) {
                            if (remKW <= intFslab) {
                                arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                arrFrate[i] = intFrate;
                                arrFslab[i] = ((ccdl_count + 1) * remKW);
                                arrdlFslab[i] = remKW;
                                i = 11;
                            } else if (remKW > intFslab) {
                                arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                remKW = remKW - intFslab;
                                arrFrate[i] = intFrate;
                                arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                arrdlFslab[i] = intFslab;
                            }
                        }

                        if (i == 5) {
                            if (remKW <= intFslab) {
                                arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                arrFrate[i] = intFrate;
                                arrFslab[i] = ((ccdl_count + 1) * remKW);
                                arrdlFslab[i] = remKW;
                                i = 11;
                            } else if (remKW > intFslab) {
                                arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                remKW = remKW - intFslab;
                                arrFrate[i] = intFrate;
                                arrFslab[i] = ((ccdl_count + 1) * remKW);
                                arrdlFslab[i] = intFslab;
                            }
                        }
                    } else {
                        if (KW == 1) {
                            String fslab = c.getString(c.getColumnIndex("FSLAB" + i));
                            int intFslab = Integer.parseInt(fslab);
                            String frate = c.getString(c.getColumnIndex("FRATE" + i));
                            double intFrate = Double.parseDouble(frate);
                            loadfrate = c.getString(c.getColumnIndex("FRATE2"));
                            if (i == 1) {
                                if (KW <= intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * KW) * intFrate;
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * KW);
                                    arrdlFslab[i] = KW;
                                    i = 11;
                                } else if (KW > intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                    remKW = KW - intFslab;
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                    arrdlFslab[i] = intFslab;
                                }
                            }

                            if (i == 2) {
                                if (remKW <= intFslab) {
                                    arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                    arrFrate[i] = intFrate;
                                    frate_2 = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * remKW);
                                    arrdlFslab[i] = remKW;
                                    i = 11;
                                } else if (remKW > intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                    remKW = remKW - intFslab;
                                    arrFrate[i] = intFrate;
                                    frate_2 = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                    arrdlFslab[i] = intFslab;
                                }
                            }

                            if (i == 3) {
                                if (remKW <= intFslab) {
                                    arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * remKW);
                                    arrdlFslab[i] = remKW;
                                    i = 11;
                                } else if (remKW > intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                    remKW = remKW - intFslab;
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                    arrdlFslab[i] = intFslab;
                                }
                            }

                            if (i == 4) {
                                if (remKW <= intFslab) {
                                    arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * remKW);
                                    arrdlFslab[i] = remKW;
                                    i = 11;
                                } else if (remKW > intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                    remKW = remKW - intFslab;
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                    arrdlFslab[i] = intFslab;
                                }
                            }

                            if (i == 5) {
                                if (remKW <= intFslab) {
                                    arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * remKW);
                                    arrdlFslab[i] = remKW;
                                    i = 11;
                                } else if (remKW > intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                    remKW = remKW - intFslab;
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * remKW);
                                    arrdlFslab[i] = intFslab;
                                }
                            }
                        } else {
                            String fslab = c.getString(c.getColumnIndex("FSLAB" + i));
                            int intFslab = Integer.parseInt(fslab);
                            if (Rrebate.equals("0")) {
                                frate = c.getString(c.getColumnIndex("FRATE" + (i + 1)));
                            }
                            if (Rrebate.equals("1")) {
                                frate = c.getString(c.getColumnIndex("FRATE" + (i + 1)));
                            }
                            loadfrate = c.getString(c.getColumnIndex("FRATE2"));
                            double intFrate = Double.parseDouble(frate);
                            if (i == 1) {
                                if (KW <= intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * KW) * intFrate;
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * KW);
                                    arrdlFslab[i] = KW;
                                    i = 11;
                                } else if (KW > intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                    remKW = KW - intFslab;
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                    arrdlFslab[i] = intFslab;
                                }
                            }

                            if (i == 2) {
                                if (remKW <= intFslab) {
                                    arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                    arrFrate[i] = intFrate;
                                    frate_2 = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * remKW);
                                    arrdlFslab[i] = remKW;
                                    i = 11;
                                } else if (remKW > intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                    remKW = remKW - intFslab;
                                    arrFrate[i] = intFrate;
                                    frate_2 = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                    arrdlFslab[i] = intFslab;
                                }
                            }

                            if (i == 3) {
                                if (remKW <= intFslab) {
                                    arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * remKW);
                                    arrdlFslab[i] = remKW;
                                    i = 11;
                                } else if (remKW > intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                    remKW = remKW - intFslab;
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                    arrdlFslab[i] = intFslab;
                                }
                            }

                            if (i == 4) {
                                if (remKW <= intFslab) {
                                    arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * remKW);
                                    arrdlFslab[i] = remKW;
                                    i = 11;
                                } else if (remKW > intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                    remKW = remKW - intFslab;
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * intFslab);
                                    arrdlFslab[i] = intFslab;
                                }
                            }

                            if (i == 5) {
                                if (remKW <= intFslab) {
                                    arrFC[i] = (((ccdl_count + 1) * remKW) * intFrate);
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * remKW);
                                    arrdlFslab[i] = remKW;
                                    i = 11;
                                } else if (remKW > intFslab) {
                                    arrFC[i] = ((ccdl_count + 1) * intFslab) * intFrate;
                                    remKW = remKW - intFslab;
                                    arrFrate[i] = intFrate;
                                    arrFslab[i] = ((ccdl_count + 1) * remKW);
                                    arrdlFslab[i] = intFslab;
                                }
                            }
                        }
                    }
                }
            }
            for (int j = 0; j <= 5; j++) {
                FC = FC + arrFC[j];
            }
        } else {
            if (tariff.equals("70")) {
                c.moveToNext();
                String frate = c.getString(c.getColumnIndex("FRATE1"));
                double frate1 = Double.parseDouble(frate);
                double cal_days = frate1 / 7;
                double days = Double.parseDouble(NoofWeeks);
                FC = days * cal_days * KW;
                arrFslab[1] = KW;
                arrFrate[1] = frate1;
                arrFC[1] = days * cal_days * KW;
				/*if (NoofWeeks.equals("1")) {
					String frate = c.getString(c.getColumnIndex("FRATE1"));
					double frate1 = Double.parseDouble(frate);
					FC = frate1 * KW;
                    arrFslab[1] = KW;
                    arrFrate[1] = frate1;
                    arrFC[1] = frate1 * KW;
				}
				else {
					if (NoofWeeks.equals("2")) {
						String frate = c.getString(c.getColumnIndex("FRATE2"));
						double frate1 = Double.parseDouble(frate);
						FC = frate1 * KW;
                        arrFslab[1] = KW;
                        arrFrate[1] = frate1;
                        arrFC[1] = frate1 * KW;
					}
					else {
						if (NoofWeeks.equals("3")) {
							String frate = c.getString(c.getColumnIndex("FRATE3"));
							double frate1 = Double.parseDouble(frate);
							FC = frate1 * KW;
                            arrFslab[1] = KW;
                            arrFrate[1] = frate1;
                            arrFC[1] = frate1 * KW;
						}
						else {
							if (NoofWeeks.equals("4")) {
								String frate = c.getString(c.getColumnIndex("FRATE4"));
								double frate1 = Double.parseDouble(frate);
								FC = frate1 * KW;
                                arrFslab[1] = KW;
                                arrFrate[1] = frate1;
                                arrFC[1] = frate1 * KW;
							}
							else {
								if (NoofWeeks.equals("5")) {
									String frate = c.getString(c.getColumnIndex("FRATE5"));
									double frate1 = Double.parseDouble(frate);
									FC = frate1 * KW;
                                    arrFslab[1] = KW;
                                    arrFrate[1] = frate1;
                                    arrFC[1] = frate1 * KW;
								}
							}
						}
					}
				}*/
            }
        }
    }

    // Function to Calculate Energy
    // Consumption======================================================
    public void EcCalculation(Cursor c) {
        double intconsume = Double.parseDouble(Readingconsume);
        Units = intconsume;
        if (c.getCount() > 0) {
            String noOfSlab = c.getString(c.getColumnIndex("NOF_ESLABS"));
            int intNoOfSlab = Integer.parseInt(noOfSlab);
            for (int i = 1; i <= intNoOfSlab; i++) {
                String eslab = c.getString(c.getColumnIndex("ESLAB" + i));
                int extraintEslab = Integer.parseInt(eslab);
                intEslab = (ccdl_count + 1) * extraintEslab;
                String erate = c.getString(c.getColumnIndex("ERATE" + i));
                double intErate = Double.parseDouble(erate);
                if (i == 1) {
                    if (Units <= intEslab) {
                        arrEC[i] = Units * intErate;
                        arrErate[i] = intErate;
                        arrEslab[i] = Units;
                        i = 11;
                    } else if (Units > intEslab) {
                        arrEC[i] = intEslab * intErate;
                        remUnits = Units - intEslab;
                        arrErate[i] = intErate;
                        arrEslab[i] = intEslab;
                    }
                }

                if (i == 2) {
                    if (remUnits <= intEslab) {
                        arrEC[i] = (remUnits * intErate);
                        arrErate[i] = intErate;
                        arrEslab[i] = remUnits;
                        i = 11;
                    } else if (remUnits > intEslab) {
                        arrEC[i] = intEslab * intErate;
                        remUnits = remUnits - intEslab;
                        arrErate[i] = intErate;
                        arrEslab[i] = intEslab;
                    }
                }
                if (i == 3) {
                    if (remUnits <= intEslab) {
                        arrEC[i] = remUnits * intErate;
                        arrErate[i] = intErate;
                        arrEslab[i] = remUnits;
                        i = 11;
                    } else if (remUnits > intEslab) {
                        arrEC[i] = intEslab * intErate;
                        remUnits = remUnits - intEslab;
                        arrErate[i] = intErate;
                        arrEslab[i] = intEslab;
                    }
                }
                if (i == 4) {
                    if (remUnits <= intEslab) {
                        arrEC[i] = (remUnits * intErate);
                        arrErate[i] = intErate;
                        arrEslab[i] = remUnits;
                        i = 11;
                    } else if (remUnits > intEslab) {
                        arrEC[i] = intEslab * intErate;
                        remUnits = remUnits - intEslab;
                        arrErate[i] = intErate;
                        arrEslab[i] = intEslab;
                    }
                }
                if (i == 5) {
                    if (remUnits <= intEslab) {
                        arrEC[i] = (remUnits * intErate);
                        arrErate[i] = intErate;
                        arrEslab[i] = remUnits;
                        i = 11;
                    } else if (remUnits > intEslab) {
                        arrEC[i] = intEslab * intErate;
                        remUnits = remUnits - intEslab;
                        arrErate[i] = intErate;
                        arrEslab[i] = intEslab;
                    }
                }
            }
        }
        for (int j = 0; j <= 5; j++) {
            EC = EC + arrEC[j];
        }
        if (tariff.equals("10")) {
            if (EC < 30) {
                EC = 30;
            }
        }
    }

    // Function to Calculate BMD
    // Penalties===========================================================

    public double bmdPenalities(double bmdValue, String invenload, String pfflag, String bmdkw) {
        if (tariff.equals("30") || tariff.equals("20") || tariff.equals("21") || tariff.equals("23") || tariff.equals("61")) {
            sanctionLoad = KW;
        } else {
            if (tariff.equals("50") || tariff.equals("51") || tariff.equals("52") || tariff.equals("53") || tariff.equals("60")) {
                sanctionLoad = HP;
            }
        }
        if (pfflag.equals("0")) {
            double load = Double.parseDouble(invenload);
            if (Double.parseDouble(invenload) > 0) {
                if (tariff.equals("20") || tariff.equals("21") || tariff.equals("23")) {
                    String ad1 = loadfrate;
                    Double ad = Double.parseDouble(ad1);
                    bmdPenalty = ad * load * 2;
                } else bmdPenalty = arrFrate[1] * load * 2;
            }
        } else {
            DecimalFormat num = new DecimalFormat("##.00");
            double load2 = Double.parseDouble(fcall.decimalroundoff(bmdValue * cal_MF));
            double load3 = Double.parseDouble(num.format(Double.parseDouble(bmdkw)));
            double load4 = Double.parseDouble(num.format(load2 - load3));
            if (load4 > 0) {
                String load5 = "" + load4;
                String load6 = load5.substring(0, load5.lastIndexOf('.'));
                String load8 = load5.substring(load5.lastIndexOf('.') + 1);
                int load7 = 0;
                if (load8.length() == 1) {
                    load7 = Integer.parseInt(load8 + "0");
                } else load7 = Integer.parseInt(load8);
                double fcdouble = 0;
                if (tariff.equals("20") || tariff.equals("21") || tariff.equals("23"))
                    fcdouble = Double.parseDouble(loadfrate) * 2;
                else fcdouble = arrFrate[1] * 2;
                if (load7 >= 0 && load7 <= 12) {
                    bmdPenalty = Double.parseDouble(load6) * fcdouble;
                } else if (load7 > 12 && load7 <= 37) {
                    bmdPenalty = (Double.parseDouble(load6) + 0.25) * fcdouble;
                } else if (load7 > 37 && load7 <= 62) {
                    bmdPenalty = (Double.parseDouble(load6) + 0.50) * fcdouble;
                } else if (load7 > 62 && load7 <= 87) {
                    bmdPenalty = (Double.parseDouble(load6) + 0.75) * fcdouble;
                } else if (load7 > 87) {
                    bmdPenalty = (Double.parseDouble(load6) + 1) * fcdouble;
                }
            } else {
                bmdPenalty = 0.00;
            }
            /*if (load2 > sanctionLoad) {
                double int_load = Double.parseDouble(fcall.decimalroundoff(load2 - sanctionLoad));
                bmdPenalty = (int_load) * (FC/sanctionLoad) * 2;
            } else {
                bmdPenalty = 0.00;
            }*/
        }
        return bmdPenalty;
    }

    // Function to Calculate the Rebate of
    // Consumer==================================================
    public double billDeduCtion(Cursor c) {
        double intSolarRebateRate = 0;
        double intRebateSolarMAX_VAL = 0;
//		double intHRebate = 0;
        int flagRebatesolar = Integer.parseInt(this.flagRebate);
        if (flagRebatesolar == 1) {
            if (c.getCount() > 0) {
                double consumereading = Integer.parseInt(Readingconsume);
                String rebateSolarRate = c.getString(c.getColumnIndex("SOLAR_RATE"));
                intSolarRebateRate = Double.parseDouble(rebateSolarRate);
                String rebateSolarMaxValue = c.getString(c.getColumnIndex("SOLAR_MAX_VAL"));
                intRebateSolarMAX_VAL = Double.parseDouble(rebateSolarMaxValue);
                if (ccdl_count > 0)
                    intRebateSolarMAX_VAL = (ccdl_count + 1) * intRebateSolarMAX_VAL;
                double deductcalculate = consumereading * intSolarRebateRate;
                if (deductcalculate > intRebateSolarMAX_VAL) {
                    finalRebate = intRebateSolarMAX_VAL;
                } else finalRebate = deductcalculate;
            }
        }
        if (tariff.equals("30")) {
            if (flagRebatesolar == 2) {
                if (c.getCount() > 0) {
                    double consumereading = Integer.parseInt(Readingconsume);
                    String hRebate = c.getString(c.getColumnIndex("HREBATE_PER"));
                    double intHRebate = Double.parseDouble(hRebate);
                    finalRebate = consumereading * intHRebate;
                }
            }
        }
        if (tariff.equals("20") || tariff.equals("21")) {
            if (flagRebatesolar == 7) {
                double consumereading = Integer.parseInt(Readingconsume);
                String charityrate = c.getString(c.getColumnIndex("CHARITY_RATE"));
                double charity = Double.parseDouble(charityrate);
                finalRebate = consumereading * charity;
            }
        }
        return finalRebate;
    }

    public double charityCalculation(Cursor c) {
        int flagRebatecharity = Integer.parseInt(this.flagRebate);
        if (tariff.equals("20") || tariff.equals("21")) {
            if (flagRebatecharity == 7) {
                double consumereading = Integer.parseInt(Readingconsume);
                String charityrate = c.getString(c.getColumnIndex("CHARITY_RATE"));
                double charity = Double.parseDouble(charityrate);
                CharityRate = consumereading * charity;
            }
        }
        return CharityRate;
    }

    // Function to Calculate the GOK

    public double gokCalculation(Cursor c) {
        if (c.getCount() > 0) {
            if (flagRebate.equals("5")) {
                String plConsumer = c.getString(c.getColumnIndex("PL_CONSUMER"));
                double int_plcons_Value = Double.parseDouble(plConsumer);
                Units = Double.parseDouble(Readingconsume);
                if (int_plcons_Value > 0) {
                    double EC_1 = EC;
                    double gok = Units * int_plcons_Value;
                    gok_payment = EC_1 - gok;
                } else {
                    gok_payment = 0.00;
                }
            } else {
                if (tariff.equals("10")) {
                    int readUnits = Integer.parseInt(Readingconsume);
                    if (readUnits <= 40) {
                        gok_payment = EC + data2;
                        additionalgok = true;
                    }
                } else {
                    if (tariff.equals("23")) {
                        double intconsume = Double.parseDouble(Readingconsume);
                        Units = intconsume;
                        dbh.openDatabase();
                        Cursor c1 = dbh.flECcount();
                        if (c1 != null) {
                            c1.moveToNext();
                            String noOfSlab = c1.getString(c1.getColumnIndex("NOF_ESLABS"));
                            int intNoOfSlab = Integer.parseInt(noOfSlab);
                            EC1 = 0;
                            for (int i = 1; i <= intNoOfSlab; i++) {
                                String eslab1 = c1.getString(c1.getColumnIndex("ESLAB" + i));
                                int extraintEslab1 = Integer.parseInt(eslab1);
                                intEslab = (ccdl_count + 1) * extraintEslab1;
                                String erate = c1.getString(c1.getColumnIndex("ERATE" + i));
                                double intErate = Double.parseDouble(erate);
                                if (i == 1) {
                                    if (Units <= intEslab) {
                                        arrEC1[i] = Units * intErate;
                                        arrE1rate[i] = intErate;
                                        arrE1slab[i] = Units;
                                        i = 11;
                                    } else if (Units > intEslab) {
                                        arrEC1[i] = intEslab * intErate;
                                        remUnits = Units - intEslab;
                                        arrE1rate[i] = intErate;
                                        arrE1slab[i] = intEslab;
                                    }
                                }

                                if (i == 2) {
                                    if (remUnits <= intEslab) {
                                        arrEC1[i] = (remUnits * intErate);
                                        arrE1rate[i] = intErate;
                                        arrE1slab[i] = remUnits;
                                        i = 11;
                                    } else if (remUnits > intEslab) {
                                        arrEC1[i] = intEslab * intErate;
                                        remUnits = remUnits - intEslab;
                                        arrE1rate[i] = intErate;
                                        arrE1slab[i] = intEslab;
                                    }
                                }
                                if (i == 3) {
                                    if (remUnits <= intEslab) {
                                        arrEC1[i] = remUnits * intErate;
                                        arrE1rate[i] = intErate;
                                        arrE1slab[i] = remUnits;
                                        i = 11;
                                    } else if (remUnits > intEslab) {
                                        arrEC1[i] = intEslab * intErate;
                                        remUnits = remUnits - intEslab;
                                        arrE1rate[i] = intErate;
                                        arrE1slab[i] = intEslab;
                                    }
                                }
                                if (i == 4) {
                                    if (remUnits <= intEslab) {
                                        arrEC1[i] = (remUnits * intErate);
                                        arrE1rate[i] = intErate;
                                        arrE1slab[i] = remUnits;
                                        i = 11;
                                    } else if (remUnits > intEslab) {
                                        arrEC1[i] = intEslab * intErate;
                                        remUnits = remUnits - intEslab;
                                        arrE1rate[i] = intErate;
                                        arrE1slab[i] = intEslab;
                                    }
                                }
                                if (i == 5) {
                                    if (remUnits <= intEslab) {
                                        arrEC1[i] = (remUnits * intErate);
                                        arrE1rate[i] = intErate;
                                        arrE1slab[i] = remUnits;
                                        i = 11;
                                    } else if (remUnits > intEslab) {
                                        arrEC1[i] = intEslab * intErate;
                                        remUnits = remUnits - intEslab;
                                        arrE1rate[i] = intErate;
                                        arrE1slab[i] = intEslab;
                                    }
                                }
                            }
                        }
                        for (int j = 0; j <= 5; j++) {
                            EC1 = EC1 + arrEC1[j];
                        }
                        int readUnits = Integer.parseInt(Readingconsume);
                        if (readUnits < 200) {
                            gok_payment = EC + FC;
                        } else {
                            gok_payment = (EC - EC1) + FC;
                        }
                    } else if (tariff.equals("40")) {
                        gok_payment = EC + data2;
                        additionalgok = true;
                    }
                }
            }
        }
        return gok_payment;
    }

    // Function for Extra Charges in the Bill like
    // Taxes,Penalties==========================================
    public void billExtraCharges(Cursor c) {
        if (!flagRebate.equals("3")) {
            String taxPer = c.getString(c.getColumnIndex("TAX_PER"));
            intTaxPer = Double.parseDouble(taxPer);
	/*		String intrPer = data.getString(data.getColumnIndex("INTR_PER"));
			dblintrPer = Double.parseDouble(intrPer);*/
            if (!tariff.equals("10")) {
                dblTax = EC * intTaxPer;
            } else {
                double readUnits = Double.parseDouble(Readingconsume);
                if (readUnits > 40) {
                    dblTax = EC * intTaxPer;
                } else {
                    dblTax = 0.00;
                }
            }
            //		String total2 = String.valueOf(dblTax);
            //		dblIntrTax = ((dblintrPer / 100) * int_calArrears);
	/*		String total23 = String.valueOf(dblIntrTax);
		} else {
			dblIntrTax = 0;*/
        } else {
            dblTax = 0.00;
        }
        if (tariff.equals("40"))
            dblTax = 0.00;
    }

    public String taxrate(Cursor c) {
        Log.d("debug", "taxrate: " + c.getCount());
        String taxPer = c.getString(c.getColumnIndex("TAX_PER"));
        double tax = Double.parseDouble(taxPer) * 100;
        String tax1 = "@" + "" + tax;
        String tax2 = tax1.substring(0, tax1.indexOf(".")) + "%";
        return tax2;
    }

    public String intrrate(Cursor c) {
        Log.d("debug", "intrrate: " + c.getCount());
        String intrPer = c.getString(c.getColumnIndex("INTR_PER"));
        double intr = Double.parseDouble(intrPer) * 100;
        String intr1 = "@" + "" + intr;
        String intr2 = intr1.substring(0, intr1.indexOf(".")) + "%";
        return intr2;
    }

    // Function to calculate the Penalties ===================================================
    public void billPenalties(Cursor c) {
        Log.d("debug", "billPenalties: " + c.getCount());
        double consumereading = Integer.parseInt(Readingconsume);
//			String sCapRate = data.getString(data.getColumnIndex("CAP_RATE"));
//			double intsCapRate = Double.parseDouble(sCapRate);
        double refpfValue = 0;
        if (pfValue > 0.85) {
            refpfValue = 0.85;
        } else if (pfValue < 0.70) {
            if (pfValue == 0) {
                refpfValue = 0;
            } else refpfValue = 0.70;
        } else {
            refpfValue = pfValue;
        }
        if (refpfValue < 0.85) {
            if (refpfValue == 0) {
                finalPfPenalitiy = 0;
            } else {
                String pen_charge = "";
                try {
                    pen_charge = c.getString(c.getColumnIndex("PF_PEN_CHARGE"));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    pen_charge = "0";
                }
                double intpfPenAmount = Double.parseDouble(pen_charge);
                double pfDiffrence = 0.85 - refpfValue;
                if (pfDiffrence <= 0.15) {
                    finalPfPenalitiy = intpfPenAmount * (pfDiffrence * consumereading);
                } else {
                    finalPfPenalitiy = (0.30 * consumereading);
                }
            }
        } else finalPfPenalitiy = 0;
    }

    // Function for Bill Amount =========================================================================
    public double billAmount() {
        bill_Amount = 0;
        if (!tariff.equals("70")) {
            if (tariff.equals("40") || tariff.equals("10")) {
                if (additionalgok) {
                    bill_Amount = (EC + FC + bmdPenalty + finalPfPenalitiy) - (handiRebate + solarRebate + (gok_payment - data2));
                } else {
                    bill_Amount = (EC + FC + bmdPenalty + finalPfPenalitiy) - (handiRebate + solarRebate + gok_payment);
                }
            } else {
                bill_Amount = (EC + FC + bmdPenalty + finalPfPenalitiy) - (handiRebate + solarRebate + gok_payment);
            }
        } else {
            if (flagRebate.equals("13")) {
                if ((EC + dblTax + data2) > FC) {
                    bill_Amount = EC;
                } else bill_Amount = FC;
            } else {
                if (FC > EC) {
                    bill_Amount = FC;
                } else {
                    bill_Amount = EC;
                }
            }

        }
        return bill_Amount;
    }

    // Function to Calculate TOD
    public double todCalculation(Cursor c) {
        Log.d("debug", "todcalculation: " + c.getCount());
        try {
            String minValue = c.getString(c.getColumnIndex("TOD_MIN"));
            double todMinRate = Double.parseDouble(minValue);
            String maxValue = c.getString(c.getColumnIndex("TOD_MAX"));
            double todMaxRate = Double.parseDouble(maxValue);
            finalTODValue = (todMinRate * tod_onPeakValue) + (todMaxRate * tod_ofPeakValue);
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return finalTODValue;
    }

    // Function to Calculate final
    // payment================================================
    public double billFinalPayment() {
        double finalBill = 0;
        if (tariff.equals("40") || tariff.equals("10")) {
            if (additionalgok) {
                finalBill = (bill_Amount + dblTax + dblIntrTax + data1 + finalTODValue + int_calArrears) - cal_deposit - finalRebate - CharityRate;
            } else {
                finalBill = (bill_Amount + dblTax + dblIntrTax + data1 + data2 + finalTODValue + int_calArrears) - cal_deposit - finalRebate - CharityRate;
            }
        } else {
            finalBill = (bill_Amount + dblTax + dblIntrTax + data1 + data2 + finalTODValue + int_calArrears) - cal_deposit - finalRebate - CharityRate;
        }
        return finalBill;
    }

    // Reset All Values
    public void resetAllValues() {
        for (int i = 0; i <= 6; i++) {
            arrErate[i] = 0;
            arrFrate[i] = 0;
            arrEslab[i] = 0;
            arrFslab[i] = 0;
            arrdlFslab[i] = 0;
            arrEC[i] = 0;
            arrFC[i] = 0;
        }
        FC = 0;
        EC = 0;
        Readingconsume = "0";
    }

    public double[] erateForTextViews() {
        return arrErate;
    }

    public double[] frateForTextViews() {
        return arrFrate;
    }

    public double[] eslabForTextViews() {
        return arrEslab;
    }

    public double[] fslabForTextViews() {
        return arrFslab;
    }

    public double[] dl_fslabForTextViews() {
        return arrdlFslab;
    }

    public double[] ecForTextViews() {
        return arrEC;
    }

    public double[] fcForTextViews() {
        return arrFC;
    }

    public double totalEC() {
        return EC;
    }

    public double totalFC() {
        return FC;
    }

    public double tax() {
        return dblTax;
    }

    public double intr() {
        return dblIntrTax;
    }

    public double pfPenality() {
        return finalPfPenalitiy;
    }

    public String consumeUnits() {
        String units = String.valueOf(Units);
        return units;
    }
}