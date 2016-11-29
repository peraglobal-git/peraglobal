package com.peraglobal.km.crawler.util;

import java.io.File; 
import java.io.FileInputStream; 
import java.io.IOException; 
import java.io.InputStream; 
import java.text.DateFormat;
import java.text.DecimalFormat; 
import java.text.SimpleDateFormat;
import java.util.ArrayList; 
import java.util.Date;
import java.util.HashMap;
import java.util.List; 
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil; 
import org.apache.poi.hssf.usermodel.HSSFWorkbook; 
import org.apache.poi.ss.usermodel.Cell; 
import org.apache.poi.ss.usermodel.Row; 
import org.apache.poi.ss.usermodel.Sheet; 
import org.apache.poi.ss.usermodel.Workbook; 
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class POIExcelUtil 
{ 
	private int totalRows = 0; 
    private int totalCells = 0; 
    public POIExcelUtil() 
    {} 
    
    /**
     * 根据文件名和sheet索引数读取excel文件对应的sheet
     * @param fileName 
     * @param sheetNo 
     * @return 
     * @throws Exception 
     */ 
    public Map<String,Object> read(String fileName,int sheetNo) 
    { 
    	Map<String,Object> data = new HashMap<String, Object>();
        if (fileName == null || !fileName.matches("^.+\\.(?i)((xls)|(xlsx))$")) 
        { 
            return data; 
        } 
        
        boolean isExcel2003 = true; 
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) 
        { 
            isExcel2003 = false; 
        } 
        
        File file = new File(fileName); 
        if (file == null || !file.exists()) 
        { 
            return data; 
        } 
        
        try 
        { 
            data = read(new FileInputStream(file), isExcel2003,sheetNo); 
        } 
        catch (Exception ex) 
        { 
            ex.printStackTrace(); 
        } 
        
        return data; 
    } 
    
    /**
     * 获取sheet总数
     * @param String 
     * @return 
     */ 
    public int getTotalSheets(String fileName) 
    { 
    	int totalSheets = 0;
    	if (fileName == null || !fileName.matches("^.+\\.(?i)((xls)|(xlsx))$")) 
        { 
            return totalSheets; 
        } 
        
        boolean isExcel2003 = true; 
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) 
        { 
            isExcel2003 = false; 
        } 
        
        File file = new File(fileName); 
        if (file == null || !file.exists()) 
        { 
            return totalSheets; 
        } 
        
        try 
        { 
        	InputStream inputStream = new FileInputStream(file);
            Workbook wb = isExcel2003 ? new HSSFWorkbook(inputStream) 
                    : new XSSFWorkbook(inputStream); 
            totalSheets =  wb.getNumberOfSheets();
        } 
        catch (IOException e) 
        { 
            e.printStackTrace(); 
        } 
        return totalSheets; 
    } 
    
    /**
     * 根据流读取Excel文件
     * @param inputStream 
     * @param isExcel2003 
     * @return 
     */ 
    public Map<String,Object> read(InputStream inputStream, 
            boolean isExcel2003,int sheetNo) 
    { 
    	Map<String,Object> data = new HashMap<String, Object>();
    	String sheetName = "";
        List<String[]> dataLst = new ArrayList<String[]>(); 
        try 
        { 
            Workbook wb = isExcel2003 ? new HSSFWorkbook(inputStream) 
                    : new XSSFWorkbook(inputStream); 
        	sheetName = wb.getSheetName(sheetNo);
    		dataLst = read(wb,sheetName); 
        } 
        catch (IOException e) 
        { 
            e.printStackTrace(); 
        } 
        data.put("sheetName", sheetName);
        data.put("sheetNo", sheetNo);
        data.put("dataLst", dataLst);
        return data; 
    } 

    public int getTotalRows() 
    { 
        return totalRows; 
    } 
    
    public int getTotalCells() 
    { 
        return totalCells; 
    } 
    
    /** 
     *读取数据
     * @param wb 
     * @return 
     */ 
    private List<String[]> read(Workbook wb,String sheetName) 
    { 
        List<String[]> dataLst = new ArrayList<String[]>(); 
        
        Sheet sheet = wb.getSheet(sheetName); 
        this.totalRows = sheet.getPhysicalNumberOfRows(); 
        if (this.totalRows >= 1 && sheet.getRow(0) != null) 
        { 
            //this.totalCells = sheet.getRow(0).getPhysicalNumberOfCells(); 
        } 
        int rows = 0;
        int cells = 0;
        for (int r = rows; r < this.totalRows; r++) 
        { 
            Row row = sheet.getRow(r); 
            if (row == null) 
            { 
                continue; 
            } 
            
//          ArrayList<String> rowLst = new ArrayList<String>(); 
            String[] rowAry = new String[sheet.getRow(0).getPhysicalNumberOfCells()];
            //row.getPhysicalNumberOfCells()+cells
            for (int c = cells; c < sheet.getRow(0).getPhysicalNumberOfCells(); c++) 
            {
                Cell cell = row.getCell(c); 
                String cellValue = ""; 
                if (cell == null || cell.equals("")) 
                { 
                	rowAry[c] = ""; 
                    continue; 
                } 
                
                /**处理数字型的,自动去零*/ 
                if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) 
                { 
                    if (HSSFDateUtil.isCellDateFormatted(cell)) 
                    { 
                        cellValue = get4yMdHms(cell.getDateCellValue()); 
                    } 
                    else 
                    { 
                        cellValue = getRightStr(cell.getNumericCellValue() + ""); 
                    }
                } 
                else if (Cell.CELL_TYPE_STRING == cell.getCellType()) 
                { 
                    cellValue = cell.getStringCellValue(); 
                } 
                else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) 
                { 
                    cellValue = cell.getBooleanCellValue() + ""; 
                } 
                else 
                { 
                    cellValue = cell.toString() + ""; 
                }
                rowAry[c] = cellValue.trim(); 
            }
            String rowStr = "";
            for(int i = 0; i < rowAry.length; i++){
            	rowStr += rowAry[i];
            }
            if(!rowStr.equals("")){
            	dataLst.add(rowAry); 
            }
        } 
        return dataLst; 
    } 
    
    /**
     * 
     */
    private String get4yMdHms(Date d){
    	
    	DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

    	return formater.format(d);

    }
    
    /**
     * 正确地处理整数后自动加零的情况
     * @param sNum 
     * @return 
     */ 
    private String getRightStr(String sNum) 
    { 
        DecimalFormat decimalFormat = new DecimalFormat("#.000000"); 
        String resultStr = decimalFormat.format(new Double(sNum)); 
        if (resultStr.matches("^[-+]?\\d+\\.[0]+$")) 
        { 
            resultStr = resultStr.substring(0, resultStr.indexOf(".")); 
        } 
        return resultStr; 
    }
    
    /**
     * 根据文件名和sheet索引数读取excel文件对应的sheet
     * @param fileName 
     * @param sheetNo 
     * @return 
     * @throws Exception 
     */ 
    public Map<String,List<String[]>> readExpert(String fileName,int sheetNo) 
    { 
    	Map<String,List<String[]>> data = new HashMap<String,List<String[]>>();
        if (fileName == null || !fileName.matches("^.+\\.(?i)((xls)|(xlsx))$")) 
        { 
            return data; 
        } 
        
        boolean isExcel2003 = true; 
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) 
        { 
            isExcel2003 = false; 
        } 
        
        File file = new File(fileName); 
        if (file == null || !file.exists()) 
        { 
            return data; 
        } 
        
        try 
        { 
            data = readExpert(new FileInputStream(file), isExcel2003,sheetNo); 
        } 
        catch (Exception ex) 
        { 
            ex.printStackTrace(); 
        } 
        
        return data; 
    }
    /**
     * 根据流读取Excel文件
     * @param inputStream 
     * @param isExcel2003 
     * @return 
     */ 
    public Map<String,List<String[]>> readExpert(InputStream inputStream, 
            boolean isExcel2003,int sheetNo) 
    { 
    	String sheetName = "";
    	Map<String,List<String[]>> dataLst = new HashMap<String,List<String[]>>(); 
        try 
        { 
            Workbook wb = isExcel2003 ? new HSSFWorkbook(inputStream) 
                    : new XSSFWorkbook(inputStream); 
        	sheetName = wb.getSheetName(sheetNo);
    		dataLst = readExpert(wb,sheetName); 
        } 
        catch (IOException e) 
        { 
            e.printStackTrace(); 
        } 
        return dataLst; 
    }
    /** 
     * 读取专家数据
     * @param wb 
     * @return 
     */ 
    private Map<String,List<String[]>> readExpert(Workbook wb,String sheetName) 
    { 
    	Map<String,List<String[]>> dataLst = new HashMap<String,List<String[]>>(); 
        
        Sheet sheet = wb.getSheet(sheetName); 
        this.totalRows = sheet.getPhysicalNumberOfRows(); 
        if (this.totalRows >= 1 && sheet.getRow(0) != null) 
        { 
            //this.totalCells = sheet.getRow(0).getPhysicalNumberOfCells(); 
        } 
        int rows = 0;
        int cells = 0;
        List<String[]> rowLst = null;
        for (int r = rows; r < this.totalRows; r++) 
        { 
        	rowLst = new ArrayList<String[]>();
            Row row = sheet.getRow(r); 
            if (row == null) 
            { 
                continue; 
            } 
            
//          ArrayList<String> rowLst = new ArrayList<String>(); 
            String[] rowAry = new String[sheet.getRow(0).getPhysicalNumberOfCells()];
            //row.getPhysicalNumberOfCells()+cells
            for (int c = cells; c < sheet.getRow(0).getPhysicalNumberOfCells(); c++) 
            {
                Cell cell = row.getCell(c); 
                String cellValue = ""; 
                if (cell == null || cell.equals("")) 
                { 
                	rowAry[c] = ""; 
                    continue; 
                } 
                
                /**处理数字型的,自动去零*/ 
                if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) 
                { 
                    if (HSSFDateUtil.isCellDateFormatted(cell)) 
                    { 
                        cellValue = get4yMdHms(cell.getDateCellValue()); 
                    } 
                    else 
                    { 
                        cellValue = getRightStr(cell.getNumericCellValue() + ""); 
                    }
                } 
                else if (Cell.CELL_TYPE_STRING == cell.getCellType()) 
                { 
                    cellValue = cell.getStringCellValue(); 
                } 
                else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) 
                { 
                    cellValue = cell.getBooleanCellValue() + ""; 
                } 
                else 
                { 
                    cellValue = cell.toString() + ""; 
                }
                rowAry[c] = cellValue.trim(); 
            } 
            if(dataLst.get(rowAry[0]) != null){
            	rowLst = (ArrayList<String[]>)dataLst.get(rowAry[0]);
            }
            rowLst.add(rowAry);
            dataLst.put(rowAry[0], rowLst); 
        } 
        return dataLst; 
    }
    
} 
