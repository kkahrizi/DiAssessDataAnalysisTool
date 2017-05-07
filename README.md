# DiAssessDataAnalysisTool

A tool for analyzing data at DiAssess.

Version 2.0
Two utilities: Compilation of PCR Tubes, Analysis of Thermocycler Data
- PCR Tube Compilation Usage:
1. Download the entire folder of timelapse images to your computer.
	1a. All folders must have the same number of images.
	2a. All image names must be associated with the correct time. (i.e. "0000.jpg", "1200.jpg")
2. Run DiAssessDataAnalysisTool for Analaysis of PCR Tubes. 
3. Navigate to the appropriate downloaded folder of images.
4. Label PCR tubes following onscreen cues.
5. Save compiled image to a location of your choice (include .png in image name to ensure your computer correctly interprets the file as an image)

- Thermocycler Analysis Usage:
1. Export thermocycler data as ".csv" using Biorad CFX Connect software. 
2. Save set of .csv files to a folder on your computer. 
	2a. Both RFU data "<EXPERIMENT NAME> - Quantification Amplification Results_SYBR.csv" and
	sample data "<EXPERIMENT NAME> - Quantification Summary_0.csv" must be contained within folder.
3. Run DiAssessDataAnalysisTool for Analysis of Thermocycler Data.
4. Navigate to the appropriate folder of thermocycler data.
5. For accurate TTR data, enter the seconds per cycle as measured during your thermocycler run
6. Select option for plotting TTR data on plots (TTR will be calculated regardless and can be exported to table)
	6a. Currently, the only TTR method implemented is using midpoint method. 
7. Select "Continue". An image should appear with all thermocycler plots. 
8. Use buttons at bottom of screen to save TTR table and plots to files. 


Installation Instructions:
1. Download the entire project to your desktop
2. Extract in an easy to access location
3. Run using dist/DiAssessDataAnalysisTool.jar

Make sure you have java installed, and give the file execution privileges.

Version 2.01 Updates:
- Made fonts on UI bigger for ease of reading on high DPI monitors
- Fixed bug where some plots were not being saved due to indexing error
- Included option for number of plots per row

Version 2.02 Updates:
- Included summary statistics (mean, stdev) in thermocycler TTR data table


