# Hebrew-Interlinear-Old-Testament-Android
This is an updated Version of the HIOT Project: by [@stefankmitph]( https://github.com/stefankmitph ) find it here https://github.com/stefankmitph/hiot

### Updated build files as on 04/11/2019
Folowing updates for project to work with Android 3.4.2
- minSdkVersion 16
- compileSdkVersion 28
- buildToolsVersion "28.0.3"

gradle version 3.4.2

gradle-wrapper properties : 5.1.1

## Get it on Google Play

[![googleplaylink](https://github.com/peace-shillong/Hebrew-Interlinear-Old-Testament-Android/blob/master/images/play_min.png "Google Play Link")](https://play.google.com/store/apps/details?id=peace_shillong.hiot "Play Store link")

Click on the image to open google play store

### Screenshot

![screen](https://github.com/peace-shillong/Hebrew-Interlinear-Old-Testament-Android/blob/master/images/hiot_design_1.jpg "Scrennshot") 

### Todo 

	1. [ ] Bookmark Verse
	2. [ ] Search for text in a particular Book

### Change log v1.0.1

	1. Fixed typos in database 
		- 2 Kings 25
		- Ruth 4
		- 2 Samuel 24
		- 1 Chronicles 29
		- 2 Chronicles 36
		- Ecclesiastes 12
		- Isaiah 66
		- Joel 3



### Change log v1.0.0

	1. Base Packaged name changed from stefankmitp to peace_shillong

	2. Changed from inheriting from ActionBarActivity to inherit from AppCompatActivity as seen in Issue 1

	3. Added: Export Verse as Image and Share Verse as Image option 
		To allow user to export/share Verse as image, and a work around for Issue 2 (Skiped Export to PDF, unable to write hebrew text)

	4. Database Changes: Fixed/added and checked
		 - Ps. 121:3 Mistake fixed
		 - Hosea 14:1 no errors
		 - Ps 1-8 no errors
		 - Added Daniel chapter 12:13		 
		 - Proverbs 31:31 added 
		 - Leviticus 27:34 is missing: has been added
		 - Malachi 4:6. Missing: has been added
		 - Deuteronomy 34:12 Missing: has been added		 
		 - Last verse of each book are missing  (39 verses): Added the missing verse of each book


	5. Added unused library: apwlibrary 
		Unable to export pdf with Hebrew text, we can try itext7 later and see if it works

	6. Changed the activity_selection.xml layout inside the res->layout folder

	7. Added request for Storage permissions for android N and above

	8. Migrated to AndroidX artifacts

	9. Spinners in Selection now displays Books, Chapters and Verse in Dialog box for easy access

	
### Special Thanks

  [@joshhazelhurst123]( https://github.com/joshhazelhurst123 ) for correcting and adding the missing text from the sqlite database
