# Autonomous Insurance Claims Processing Agent

## Overview
This project is a Java-based application that processes FNOL (First Notice of Loss) insurance documents.  
It extracts key information, checks for missing mandatory fields, applies business rules, and routes claims to the appropriate workflow with a clear explanation.

The application supports both PDF and TXT files and produces the output in JSON format.

---

## Features
- Reads FNOL documents in PDF and TXT formats
- Extracts key claim-related fields using keyword-based detection
- Identifies missing mandatory fields
- Applies predefined routing rules
- Outputs results in structured JSON
- Accepts input file name dynamically (no code changes required)

---

## Routing Rules
- If any mandatory field is missing → Manual Review
- If the description contains keywords like fraud, inconsistent, or staged → Investigation Flag
- If the claim indicates an injury → Specialist Queue
- Otherwise → Fast-track

---

## Technologies Used
- Java
- Apache PDFBox
- Jackson (JSON processing)

---

## Project Structure

├── Main.java
├── fnol.pdf        (sample input)
├── fnol.txt        (optional sample input)
├── README.md


## How to Run

### Prerequisites
- Java JDK installed
- Required JAR files:
  - pdfbox
  - fontbox
  - commons-logging
  - jackson-databind
  - jackson-core
  - jackson-annotations

### Compile
```bash
javac -cp ".;*" Main.java


### Run 

java -cp ".;*" Main your_file_name.pdf


Eg: 

java -cp ".;*" Main fnol.pdf

OR

java -cp ".;*" Main fnol.txt
