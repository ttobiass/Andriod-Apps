# Andriod-Apps

These applications were developed by Tiffini Tobiasson in the Spring of 2018 for the Mobile Application Development (CS442) class at the Illinois Institute of Technology. All applications were written solely by me as assignments during the course. 

The Know Your Government and NewsGateway applications utilize Google API's. To protect my own personal keys, the API strings have been replaced with filler text. 

# Multi-Note
The assignment was to develop an application that would allow users to create and maintain multiple notes at once. The home page holds a recycler view where each note in the list is represented within its own card that displays the title, date/time last edited, and the first 80 characters of the note. The app also uses menu items to allow used to create a new note from the home page, or visit the information page. 

Clicking on a note or the "new note" menu item launches the edit activity. On the edit page users are able to edit/create the title and contents of the note. Once a user is finished they can press the save button on the menu bar to save their note and return to the home screen. The note is then added or updated in the list. New notes/updated notes are placed on the top of the list. 

If a user pushes the back button to return to the home activity from the edit activity, a dialog box will pop up alerting the user that the note has not been saved and asking if they would like to save it or not. If the user selects save, it will save the note, otherwise the note will be lost and the app will return to the home activity. If a user tries to save a note without adding a title, a dialog box will open up notifying the user that this is an invalid action and a title must be added. The same will happen if a user presses the back button to leave the edit activity. 

Users can also delete notes by long clicking them in the home menu. When this is done a dialog box will prompt to confirm that the user wishes to delete the note. If confirmed the note is deleted and removed from the list, otherwise it will remain unchanged. 

All note data is saved to a JSON file on the device upon apps destruction. Upon app load, the JSON file is loaded asynchronously and the list is populated. 

# Stock Watch
Description coming soon

# Know Your Government
Description coming soon

# News Gateway
Description coming soon

