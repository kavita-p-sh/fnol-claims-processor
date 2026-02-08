import java.io.File;
import java.nio.file.Files;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Main{

    public static void main(String[] args) throws Exception {

        // ===== 1. Read file name from command line =====
        if (args.length == 0) {
            System.out.println("Please provide input file name (PDF or TXT)");
            System.out.println("Example: java Main fnol.pdf");
            return;
        }

        File file = new File(args[0]);
        String text = "";

        if (!file.exists()) {
            System.out.println("File not found: " + args[0]);
            return;
        }

        // ===== 2. Read file based on extension =====
        if (file.getName().toLowerCase().endsWith(".pdf")) {

            PDDocument document = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
            document.close();

        } else if (file.getName().toLowerCase().endsWith(".txt")) {

            text = new String(Files.readAllBytes(file.toPath()));

        } else {
            System.out.println("Unsupported file format. Use PDF or TXT only.");
            return;
        }

        // ===== 3. Extract fields (keyword-based) =====
        boolean hasPolicyNumber = text.contains("POLICY NUMBER");
        boolean hasPolicyHolder = text.contains("NAME OF INSURED");
        boolean hasDateOfLoss = text.contains("DATE OF LOSS");
        boolean hasEstimateAmount = text.contains("ESTIMATE AMOUNT");
        boolean hasDescription = text.contains("DESCRIPTION");

        // ===== 4. Identify missing fields =====
        List<String> missingFields = new ArrayList<>();

        if (!hasPolicyNumber) missingFields.add("Policy Number");
        if (!hasPolicyHolder) missingFields.add("Policy Holder");
        if (!hasDateOfLoss) missingFields.add("Date Of Loss");
        if (!hasEstimateAmount) missingFields.add("Estimate Amount");
        if (!hasDescription) missingFields.add("Description");

        // ===== 5. Routing logic =====
        String recommendedRoute;
        String reasoning;

        if (!missingFields.isEmpty()) {
            recommendedRoute = "Manual Review";
            reasoning = "Mandatory fields are missing";

        } else if (text.toLowerCase().contains("fraud")
                || text.toLowerCase().contains("inconsistent")
                || text.toLowerCase().contains("staged")) {

            recommendedRoute = "Investigation Flag";
            reasoning = "Fraud related keyword found";

        } else if (text.toLowerCase().contains("injury")) {

            recommendedRoute = "Specialist Queue";
            reasoning = "Injury related claim";

        } else {
            recommendedRoute = "Fast-track";
            reasoning = "All mandatory fields present and low risk";
        }

        // ===== 6. Prepare JSON output =====
        Map<String, Object> output = new LinkedHashMap<>();

        Map<String, Boolean> extractedFields = new LinkedHashMap<>();
        extractedFields.put("Policy Number", hasPolicyNumber);
        extractedFields.put("Policy Holder", hasPolicyHolder);
        extractedFields.put("Date Of Loss", hasDateOfLoss);
        extractedFields.put("Estimate Amount", hasEstimateAmount);
        extractedFields.put("Description", hasDescription);

        output.put("extractedFields", extractedFields);
        output.put("missingFields", missingFields);
        output.put("recommendedRoute", recommendedRoute);
        output.put("reasoning", reasoning);

        // ===== 7. Print JSON output =====
        ObjectMapper mapper = new ObjectMapper();
        String jsonOutput = mapper.writerWithDefaultPrettyPrinter()
                                  .writeValueAsString(output);

        System.out.println(jsonOutput);
    }
}
