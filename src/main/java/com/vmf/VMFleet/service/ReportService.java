package com.vmf.VMFleet.service;

import com.vmf.VMFleet.reports.JasperConfig;
import com.vmf.VMFleet.reports.SimpleReportExporter;
import com.vmf.VMFleet.reports.SimpleReportFiller;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    public void generateReport() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(JasperConfig.class);
        ctx.refresh();

        SimpleReportFiller simpleReportFiller = ctx.getBean(SimpleReportFiller.class);
        simpleReportFiller.setReportFileName("vehicle.jrxml");
        simpleReportFiller.compileReport();
        simpleReportFiller.fillReport();

        SimpleReportExporter simpleExporter = ctx.getBean(SimpleReportExporter.class);
        simpleExporter.setJasperPrint(simpleReportFiller.getJasperPrint());

        simpleExporter.exportToPdf("vehicle.pdf", "sdhaval");
    }
}
