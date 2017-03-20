# DiAssessDataAnalysisTool

A tool for analyzing data at DiAssess.

Version 1.0
Only functional utility: Compilation of PCR Tubes
- Usage:
1. Downlaod the entire folder of timelapse images to your computer.
2. Run DiAssessDataAnalysisTool for Analaysis of PCR Tubes. 
3. Navigate to the appropriate downloaded folder of images.
4. Label PCR tubes following onscreen cues.
5. Save compiled image to a location of your choice (include .png in image name to ensure your computer correctly interprets the file as an image)

Version 1.0 Limitations:
1. All folder must have the same number of images.
2. All image names must be associated with the correct time. (i.e. "0000.jpg", "1200.jpg")


Installation Instructions:
1. Download the entire project to your desktop
2. Extract in an easy to access location
3. Run using dist/DiAssessDataAnalysisTool.jar

Make sure you have java installed, and give the file execution privileges.


Version 1.01 Updates:
Found a situation where first tube was not detected due to image deformity. Increased hamming window size to 61 pixels to compensate for deformity, which seems to have resolved issue.

