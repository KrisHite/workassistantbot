package ru.workassistantbot.gusev.service;

import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class Excel {
        @Value("${excel.path_users}")
        private String filePath;//Путь к файлам Excel
        @Value("${excel.path_prices}")
        private String filePricePath;//Путь к файлу с расценками
        @Autowired
        Logs logs;

    public void createExcelFile(String userName)  {//Создание нового файла, если его нет
        File newFile = new File(filePath);
        newFile.mkdirs();
        //Создаю файл
        newFile = new File(filePath, userName + ".xlsx");
        //Создаю новую книгу Excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        //Создаю новый лист в книге
        GetSheet(workbook);
        //Записываем книгу Excel в файл
        try {
            WriteBook(workbook, newFile);
        }catch (IOException e){
            logs.getLog("["+ userName +"] ---" + getClass().getName() + " -- " + e);
        }
    }
        public void createPriceFile()  {//Создание нового файла c расценками, если его нет
            if(new File(filePricePath,"Prices.xlsx").exists()){

            }else {
                File newFile = new File(filePricePath);
                newFile.mkdirs();
                //Создаю файл
                newFile = new File(filePricePath, "Prices.xlsx");
                //Создаю новую книгу Excel
                XSSFWorkbook workbook = new XSSFWorkbook();
                //Создаю новый лист в книге
                XSSFSheet sheet = workbook.createSheet("Расценки");
                //Столбцы
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Спецификация");
                headerRow.createCell(1).setCellValue("Стоимость единицы");
                //Записываем книгу Excel в файл
                try {
                    WriteBook(workbook, newFile);
                } catch (IOException e) {
                    logs.getLog("[Excel] ---" + getClass().getName() + " -- " + e);
                }
            }
        }
        public void WriteBook(XSSFWorkbook workbook, File file) throws IOException {
                FileOutputStream outputStream = new FileOutputStream(file);
                workbook.write(outputStream);
                workbook.close();
                outputStream.close();
        }

        public void AddInExcel(String name, int quantity, String userName){
                try {
                        //Открываю файл Excel
                        FileInputStream inputStream = new FileInputStream(filePath + "/" + userName + ".xlsx");
                        //Создаю экземпляр книги Excel из файла
                        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                        XSSFSheet sheet = GetSheet(workbook);
                        Row row = findRow(sheet, name);
                        //Ищет такую же строку
                        if(row != null){//Если есть добавляет значение

                                int cellValue = (int) row.getCell(1).getNumericCellValue();
                                if(cellValue == 0){
                                        row.getCell(1).setCellValue(quantity);
                                }else{
                                        row.getCell(1).setCellValue(cellValue + quantity);
                                }

                        }else {//Если нет создает новую строку
                                row = sheet.createRow(sheet.getLastRowNum() + 1);
                                row.createCell(0).setCellValue(name);
                                row.createCell(1).setCellValue(quantity);
                                row.createCell(2).setCellValue(0);//пустая ячейка для стоимости
                                row.createCell(3).setCellFormula("B" + (row.getRowNum() + 1) + "*C" + (row.getRowNum() + 1));
                        }
                                //Записываем книгу Excel в файл
                                FileOutputStream outputStream = new FileOutputStream(filePath + "/" + userName + ".xlsx");
                                workbook.write(outputStream);
                                //Закрываю все потоки
                                workbook.close();
                                inputStream.close();
                                outputStream.close();
                } catch (Exception e) {
                        logs.getLog("["+ userName +"] ---" + getClass().getName() + " -- " + e);
                }
        }
        public void AddInExcel(String name, Double quantity){//Таблица с расценками
            createPriceFile();
                try {
                        //Открываю файл Excel
                        FileInputStream inputStream = new FileInputStream(filePricePath + "/Prices.xlsx");
                        //Создаю экземпляр книги Excel из файла
                        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                        XSSFSheet sheet = workbook.getSheetAt(0);
                        Row row = findRow(sheet, name);//Ищет такую же строку
                        if(row != null){//Если находит, записывает
                                row.getCell(1).setCellValue(quantity);
                        }else {//Если не находит, создает и записывает
                                row = sheet.createRow(sheet.getLastRowNum() + 1);
                                row.createCell(0).setCellValue(name);
                                row.createCell(1).setCellValue(quantity);
                        }
                        //Записываем книгу Excel в файл
                        FileOutputStream outputStream = new FileOutputStream(filePricePath + "/Prices.xlsx");
                        workbook.write(outputStream);
                        outputStream.close();//Закрываю поток
                        workbook.close();
                        inputStream.close();
                } catch (Exception e) {
                        logs.getLog("[Excel] ---" + getClass().getName() + " -- " + e);
                }
        }
        private XSSFSheet GetSheet(XSSFWorkbook workbook){
            XSSFSheet sheet;
                if(workbook.getSheet(getTime()) == null) {//Если листа с таким названием нет
                        sheet = workbook.createSheet(getTime());//Создаю лист с названием текущей даты
                        //Столбцы
                        Row headerRow = sheet.createRow(0);
                        headerRow.createCell(0).setCellValue("Спецификация");
                        headerRow.createCell(1).setCellValue("Колличество");
                        headerRow.createCell(2).setCellValue("Стоимиость единицы");
                        headerRow.createCell(3).setCellValue("Общая стоимость");
                }else{
                        sheet = workbook.getSheet(getTime());//Иначе открываю его
                }
                return sheet;
        }

        public String getValueInExcel(String userName){//Получить примерную стоимость работ
                String dataNow = getTime();
                float price = 0;
                try {
                    //Переписываю стоимость плат из файла
                    //Открываю Excel с расценками
                    FileInputStream inputPriceStream = new FileInputStream(filePricePath + "/Prices.xlsx");
                    XSSFWorkbook priceWorkbook = new XSSFWorkbook(inputPriceStream);
                    XSSFSheet sheetOfPrice = priceWorkbook.getSheetAt(0);
                    //Открываю файл Excel c работой
                    FileInputStream inputStream = new FileInputStream(filePath + "/" + userName + ".xlsx");
                    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                    XSSFSheet sheet = workbook.getSheet(dataNow);
                    Row rowWork;
                    Row rowPrice;
                    for(int i = 1; i <= sheet.getLastRowNum(); i++){
                        rowWork = sheet.getRow(i);
                        for(int k = 1; k <= sheetOfPrice.getLastRowNum(); k++){
                            rowPrice = sheetOfPrice.getRow(k);
                            if(rowWork.getCell(0).getStringCellValue().equals(rowPrice.getCell(0).getStringCellValue())){
                                rowWork.getCell(2).setCellValue(rowPrice.getCell(1).getNumericCellValue());
                            }
                        }
                    }
                    FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                    CellValue cellValue;
                    double ref;
                        for(int i = 1; i <= sheet.getLastRowNum(); i++){
                                cellValue = evaluator.evaluate(sheet.getRow(i).getCell(3));
                                ref = cellValue.getNumberValue();
                                price = price + (float)ref;
                        }
                    //Закрываю все потоки
                    FileOutputStream outputStream = new FileOutputStream(filePath + "/" + userName + ".xlsx");
                    workbook.write(outputStream);
                    outputStream.close();//Закрываю поток
                    workbook.close();
                    inputStream.close();
                    priceWorkbook.close();
                    inputPriceStream.close();
                } catch (Exception e) {
                        logs.getLog("[Excel] ---" + getClass().getName() + " -- " + e);
                }
            return "Сегодня вы заработали примерно " + price + " рублей.";
        }

        public Row findRow(XSSFSheet sheet, String nameOfBoard){//Метод ищет наименование и возвращает строку Excel
                for(int i = 0; i <= sheet.getLastRowNum(); i++){
                        String name = sheet.getRow(i).getCell(0).getStringCellValue();
                        if(name.equals(nameOfBoard)){//Если есть такая строка
                                return sheet.getRow(i);
                        }
                }
            return null;
        }

        public String getTime(){//Метод возвращает текущую дату
                LocalDate date = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                return date.format(formatter);
        }
}
