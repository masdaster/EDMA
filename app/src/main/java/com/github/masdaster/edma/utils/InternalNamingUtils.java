package com.github.masdaster.edma.utils;

import com.github.masdaster.edma.R;

public class InternalNamingUtils {
    public static String getShipName(String internalName) {
        switch (internalName) {
            case "SideWinder":
                return "Sidewinder Mk I";
            case "Eagle":
                return "Eagle Mk II";
            case "Viper":
                return "Viper Mk III";
            case "CobraMkIII":
                return "Cobra Mk III";
            case "Type6":
                return "Type-6 Transporter";
            case "Type7":
                return "Type-7 Transporter";
            case "Asp":
                return "Asp Explorer";
            case "Empire_Trader":
                return "Imperial Clipper";
            case "Federation_Dropship":
                return "Federal Dropship";
            case "Type9":
                return "Type-9 Heavy";
            case "BelugaLiner":
                return "Beluga Liner";
            case "FerDeLance":
                return "Fer-de-Lance";
            case "Federation_Corvette":
                return "Federal Corvette";
            case "Cutter":
                return "Imperial Cutter";
            case "DiamondBack":
                return "Diamondback Scout";
            case "Empire_Courier":
                return "Imperial Courier";
            case "DiamondBackXL":
                return "Diamondback Explorer";
            case "Empire_Eagle":
                return "Imperial Eagle";
            case "Federation_Dropship_MkII":
                return "Federal Assault Ship";
            case "Federation_Gunship":
                return "Federal Gunship";
            case "Viper_MkIV":
                return "Viper Mk IV";
            case "CobraMkIV":
                return "Cobra Mk IV";
            case "Independant_Trader":
                return "Keelback";
            case "Asp_Scout":
                return "Asp Scout";
            case "Type9_Military":
                return "Type-10 Defender";
            case "Krait_MkII":
                return "Krait Mk II";
            case "TypeX":
                return "Alliance Chieftain";
            case "TypeX_2":
                return "Alliance Crusader";
            case "TypeX_3":
                return "Alliance Challenger";
            case "Krait_Light":
                return "Krait Phantom";
            default:
                return internalName;
        }
    }
}
