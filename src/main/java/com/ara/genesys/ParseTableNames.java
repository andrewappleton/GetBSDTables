package com.ara.genesys;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class ParseTableNames {

    public ParseTableNames(String file) {
        System.out.println("filename: " + file);
        String query = bdsTemplate(file);
        parseQuery(query);
        System.out.println("\n\n");
    }

    private void parseQuery(String query) {
        try {
            Statement stmt = CCJSqlParserUtil.parse(query);
            Select selectStatement = (Select) stmt;
            TablesNamesFinder tables = new TablesNamesFinder();
            List<String> tablesList = tables.getTableList(selectStatement);
            for (String tname : tablesList) {
                System.out.println(tname);
            }
        } catch (JSQLParserException e) {
            System.out.println("No tables found!");
        }
    }

    private JSONObject readJSON(String filename) {
        InputStream is = null;
        JSONObject result = null;
        try {
            is = new FileInputStream(filename);
            JSONTokener tokener = new JSONTokener(is);
            result = new JSONObject(tokener);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
        return result;
    }

    private String bdsTemplate(String resource) {
        String query = "";
        JSONObject object = readJSON(resource).getJSONObject("datasets");
        object = object.getJSONObject(object.keys().next());
        System.out.println("Source: " + object.getString("source"));
        query = object.getJSONObject("primary_statements").getString("statement");
        //System.out.println(query);
        return query;
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            new ParseTableNames(args[0]);
        } else {
            System.out.println("Usage: GetBSDTables.jar <filename.tpl>");
        }
    }

}