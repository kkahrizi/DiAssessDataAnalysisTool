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

Version 2.03 Updates:
- Created slide bar to set font size, which stores data in "fontSize.conf" in dist folder

Version 2.04 Updates:
- Added ability to interpret melt curves and plot them in separate window

Version 2.05 Updates:
- Modified melt curve analyses to look at derivative of melt curve, instead of absolute RFU values

Version 2.06 Updates:
- Included peak calling of melt curves
- Output file now default opens to same directory as input thermocycler files
- Cleaned up UI a bit

Version 2.07 Updates:
- Modified thermo loading frame to be resizable (problematically big on non-high DPI screens)

Version 2.08 Updates:
- Output TTR table now includes well coordinates, column headers
- Extended support for non-SYBR dyes

Version 2.09 Updates:
- Fixed bug where sometime melt curve labels were not correctly being read
- Set fixed y-axis bounds for both melt curves and amplification curves

Version 2.10 Updates:
- Fixed bug where sample names that were just numbers were not being recognized
- Allowed for negative values in melt curve

Version 2.11 Updates: 
- Created an option to automatically save Thermocycler data plots and tables to same location as input data
- Created an option to fix y-axes or to allow automatic re-scaling of each plot individually
- Implemented inflection point TTR algorithm - TTR is the first peak value of the second derivative (where second derivative looks forward 3 values)

Version 2.12 Updates:
- Created an option to set a threshold for midpoint TTR formula

Version 2.13 Updates:
- Cleaned up UI for Thermocycler options window
- Changed default seconds per cycles setting in Thermocycler options window to 38 seconds
- Built functionality to allow for reordering of plots. To reorder plots, create a table (.csv) with one column, where each row is a sample
as found in (...) - Quantification Summary_0.csv Thermocycler file, in the order desired. In the Thermocycler options window, turn the "automatically order plots" button 
to the off position. The program will prompt you to select your .csv file, and will plot graphs in that order. 

Version 2.14 Updates:
- Built compatibility with unicode text files
- Built ability to read Cq values as output by thermal cycler and use in place of TTR