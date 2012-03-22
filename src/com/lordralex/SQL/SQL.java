/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lordralex.SQL;

import com.lordralex.SQL.SQLDataException;
import java.sql.ResultSet;
import java.sql.SQLException;
import lib.PatPeter.SQLibrary.MySQL;
import net.fissionstudios.logger.ReportLogger;

/**
 *
 * @author Joshua
 */
public class SQL {
    
    private static MySQL mysql;
    
    public static void connect(String host, String port, String db, String user, String pass)
    {
        mysql = new MySQL(ReportLogger.log, "Reporter", host, String.valueOf(port), db, user, pass);
        mysql.open();
        doesDBExist();
    }
    
    private static void doesDBExist()
    {
        if(!mysql.checkTable("reports"))
            mysql.query("CREATE TABLE reports(id, user, reportuser, report, time);");
        if(!mysql.checkTable("warnings"))
            mysql.query("CREATE TABLE reports(playername, level);");
    }
    
    public static int getWarns(String name) throws SQLDataException
    {
        ResultSet rs = mysql.query("SELECT level FROM warnings WHERE playername=" + name);
        try{
            if(rs.first())
                return rs.getInt("level");
        } catch (SQLException e){
        }
        throw new SQLDataException("Error getting " + name + "'s warning leve");
    }
    
    public static void addWarn(String name) throws SQLDataException {
        mysql.query("UPDATE warnings SET level=" + (getWarns(name) + 1) + " WHERE playername=" + name);
    }
    
    public static void removeWarn(String name) throws SQLDataException {
        int newWarn = (getWarns(name) - 1);
        if(newWarn < 0)
            newWarn = 0;
        mysql.query("UPDATE warnings SET level=" + newWarn + " WHERE playername=" + name);
    }
    
    public static void runCommand(String command)
    {
        mysql.query(command);
    }
}
