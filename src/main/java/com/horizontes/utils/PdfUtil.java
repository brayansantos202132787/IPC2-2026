/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.utils;

import com.horizontes.models.Pago;
import com.horizontes.models.Reservacion;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import jakarta.servlet.http.HttpServletResponse;
 
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author braya
 */
public class PdfUtil {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
 
    private PdfUtil() {}
 
    
    public static void generarComprobantePago(
            HttpServletResponse resp,
            Reservacion reservacion,
            List<Pago> pagos,
            BigDecimal totalPagado) throws IOException {
 
        String filename = "comprobante_" + reservacion.getNumeroReservacion() + ".pdf";
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
 
        OutputStream out = resp.getOutputStream();
        PdfWriter  writer   = new PdfWriter(out);
        PdfDocument pdfDoc  = new PdfDocument(writer);
        Document   document = new Document(pdfDoc);
 
        PdfFont bold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
 
        
        document.add(new Paragraph("HORIZONTES SIN LÍMITES")
                .setFont(bold).setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.DARK_GRAY));
 
        document.add(new Paragraph("Comprobante de Pago")
                .setFont(regular).setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER));
 
        document.add(new Paragraph("Fecha de emisión: " +
                LocalDate.now().format(FMT))
                .setFont(regular).setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT));
 
        document.add(new Paragraph("\n"));
 
        // -------- Datos de la reservación --------
        document.add(new Paragraph("Datos de la Reservación")
                .setFont(bold).setFontSize(12));
 
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .setWidth(UnitValue.createPercentValue(100));
 
        addRow(infoTable, "N° Reservación:",  reservacion.getNumeroReservacion(), bold, regular);
        addRow(infoTable, "Paquete:",         reservacion.getNombrePaquete(),      bold, regular);
        addRow(infoTable, "Fecha de viaje:",  reservacion.getFechaViaje() != null
                ? reservacion.getFechaViaje().format(FMT) : "-",                   bold, regular);
        addRow(infoTable, "Pasajeros:",       String.valueOf(reservacion.getCantidadPasajeros()), bold, regular);
        addRow(infoTable, "Agente:",          reservacion.getNombreUsuario(),       bold, regular);
        addRow(infoTable, "Estado:",          reservacion.getNombreEstado(),        bold, regular);
        addRow(infoTable, "Costo total:",     "Q. " + reservacion.getCostoTotal(),  bold, regular);
 
        document.add(infoTable);
        document.add(new Paragraph("\n"));
 
        // -------- Detalle de pagos --------
        document.add(new Paragraph("Detalle de Pagos")
                .setFont(bold).setFontSize(12));
 
        Table pagosTable = new Table(UnitValue.createPercentArray(new float[]{15, 25, 35, 25}))
                .setWidth(UnitValue.createPercentValue(100));
 
        // Encabezados de la tabla
        for (String header : new String[]{"# Pago", "Fecha", "Método", "Monto"}) {
            pagosTable.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setFont(bold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }
 
        int i = 1;
        for (Pago pago : pagos) {
            pagosTable.addCell(String.valueOf(i++));
            pagosTable.addCell(pago.getFechaPago() != null ? pago.getFechaPago().format(FMT) : "-");
            pagosTable.addCell(pago.getNombreMetodo());
            pagosTable.addCell("Q. " + pago.getMonto());
        }
 
        // Fila total
        pagosTable.addCell(new Cell(1, 3)
                .add(new Paragraph("TOTAL PAGADO").setFont(bold))
                .setTextAlignment(TextAlignment.RIGHT));
        pagosTable.addCell(new Cell()
                .add(new Paragraph("Q. " + totalPagado).setFont(bold)));
 
        document.add(pagosTable);
        document.add(new Paragraph("\n"));
 
        // -------- Pie de página --------
        document.add(new Paragraph(
                "Este documento es un comprobante oficial de pago emitido por Horizontes Sin Límites.\n" +
                "Gracias por preferirnos.")
                .setFont(regular).setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY));
 
        document.close();
    }
 
    private static void addRow(Table t, String label, String value,
                               PdfFont bold, PdfFont regular) {
        t.addCell(new Cell().add(new Paragraph(label).setFont(bold)));
        t.addCell(new Cell().add(new Paragraph(value != null ? value : "-").setFont(regular)));
    }
}
