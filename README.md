# SHACL Validator Jena-apache to RDF Data Dockerized

This repository contains a Dockerized version of the SHACL Validator tool that uses Apache Jena.

## Table of Contents

- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Output](#output)
- [Contributing](#contributing)

## Introduction

The SHACL Validator is a Java application that validates a data graph against a shape graph using the SHACL (Shapes Constraint Language) specification. It utilizes the Apache Jena library for RDF processing SHACL API for validation.
The SHACL Validator Dockerized project provides a convenient way to run the SHACL Validator tool using Apache Jena in a Docker container.

## Prerequisites
- Java Development Kit (JDK) 8 or higher.
- Apache Maven.
- Docker: Make sure you have Docker installed on your machine.

## Installation

1. Clone the repository:

   ```sh
   git clone https://github.com/Daham-Mustaf/SHACL-to-Validate-RDF-Jena-apache-Data.git


## Usage

1. Save your data graph file `src/main/data` directory of the project.
2. Save your shape graph file `src/main/shape` directory of the project.
3. Run the `ShaclDemo` class as a Java application.
4. The validation report will be generated and saved in the `src/main/reports/` directory.

## Configuration

You can modify the file paths by updating the following variables in the `ShaclValidation` class:

- `dataFileName`: The name of the data graph file.
- `shapeFileName`: The name of the shape graph file.

## Output

- If the data graph conforms to the shape graph, a conform report file will be generated in the `src/main/reports/` directory. The file will have the name format: `dataFileName_conform_report.ttl`.
- If the data graph does not conform to the shape graph, a validation report file will be generated in the `src/main/reports/` directory. The file will have the name format: `dataFileName_validation_report.ttl`.

The report files are in Turtle format and contain information about the validation results.

## Contributing

Contributions to this project are welcome! If you find any issues or have suggestions for improvements, please feel free to open an issue or submit a pull request.




