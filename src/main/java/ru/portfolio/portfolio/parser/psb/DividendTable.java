package ru.portfolio.portfolio.parser.psb;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.util.CellRangeAddress;
import ru.portfolio.portfolio.pojo.CashFlowEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static ru.portfolio.portfolio.parser.psb.PsbBrokerReport.EMTPY_RANGE;
import static ru.portfolio.portfolio.parser.psb.PsbBrokerReport.convertToInstant;

@Slf4j
public class DividendTable {
    private static final String TABLE_START_TEXT = "Выплата дивидендов";
    @Getter
    private final List<Row> data = new ArrayList<>();

    public DividendTable(PsbBrokerReport report) {
        this.data.addAll(pasreTable(report, TABLE_START_TEXT, 1));
    }

    private static List<Row> pasreTable(PsbBrokerReport report, String tableName, int leftColumn) {
        CellRangeAddress address = report.getTableCellRange(tableName);
        if (address == EMTPY_RANGE) {
            return Collections.emptyList();
        }
        List<Row> data = new ArrayList<>();
        for (int rowNum = address.getFirstRow() + 2; rowNum <= address.getLastRow(); rowNum++) {
            org.apache.poi.ss.usermodel.Row row = report.getSheet().getRow(rowNum);
            if (row != null) {
                Collection<Row> dividendOrTax = getDividendAndTax(row, leftColumn);
                if (dividendOrTax != null) {
                    data.addAll(dividendOrTax);
                }
            }
        }
        return data;
    }

    private static Collection<Row> getDividendAndTax(org.apache.poi.ss.usermodel.Row row, int leftColumn) {
        try {
            Row.RowBuilder builder = Row.builder()
                    .timestamp(convertToInstant(row.getCell(leftColumn).getStringCellValue()))
                    .event(CashFlowEvent.DIVIDEND)
                    .isin(row.getCell(leftColumn + 5).getStringCellValue())
                    .count(Double.valueOf(row.getCell(leftColumn + 7).getNumericCellValue()).intValue())
                    .value(BigDecimal.valueOf(row.getCell(leftColumn + 10).getNumericCellValue()))
                    .currency(row.getCell(leftColumn + 14).getStringCellValue());
            Collection<Row> data = new ArrayList<>();
            data.add(builder.build());
            data.add(builder
                    .event(CashFlowEvent.TAX)
                    .value(BigDecimal.valueOf(row.getCell(leftColumn + 11).getNumericCellValue()).negate())
                    .currency(row.getCell(leftColumn + 12).getStringCellValue())
                    .build());
            return data;
        } catch (Exception e) {
            log.warn("Не могу распарсить таблицу 'Погашения купонов и ЦБ' в строке {}", row.getRowNum(), e);
            return null;
        }
    }

    @Getter
    @Builder(toBuilder = true)
    public static class Row {
        private String isin;
        private Instant timestamp;
        private CashFlowEvent event;
        private int count;
        private BigDecimal value; // НКД, амортизация или налог
        private String currency; // валюта
    }
}
