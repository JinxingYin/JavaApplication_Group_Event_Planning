# JavaApplication_Group_Event_Planning

The motivation came from my friend, he was planning a trip with his high school friends after his college graduation. 
He and one other person were planning the trip for a group of 8, however, he found it difficult to keep everyone in the loop while they were away at college. 
Group chats were made but they would get clogged up with random messages and his friends would keep asking questions that were already addressed in the chat. 
This app aims to eliminate those worries and keep an organized view of the whole trip. A polling feature that would allow people in the group to vote on things to do during the event, 
a group chat specific to each event, and a separate Itinerary for each event which allows the host to keep a detailed schedule of the whole event. 


It turned out that I could design my interface with frame layouts for some of the view objects. For me, it was really interested as all the projects I encountered before did not have a hierarchy for the layout. 
They were mostly just a single level so I had to squeeze all the views into a single layout. In comparison to this, the frame layout gave me the opportunity to settle some of the buttons/icons into different levels which gave me more space for the mainstream of the UI. 
Meanwhile, I also utilized the recycle view instead of the traditional list view. I found out that, the recycler view had better animations and performances as I updated my user interface. However, I did encounter some obstacles in implementing my UI this way. I had a hard time properly working with the recycler view as the “onClickListener” method for this was not that straightforward. After some research, I overrode these functions so that they eventually got to work. These are some uniquenesses of our UI design.


	For the design of the application activity, I split the works into different portions with respect to the different features of my application. By the way, I also exploited the preference manager to pass some data locally throughout activities. With the help of this implementation trick, I eliminated some unnecessary remote queries with Firebase FireStore.
	
	
 For the chattings and creating group events I mostly utilized the Firebase Firestore database to interact with my data. There was massive data I needed to handle within some applications with chatting as one of the features. I had to manage every field required by the messages and the events and properly stored them in some places for later updating and querying. After looking up some tutorials on using the firebase fire store, I was able to correctly update the documents and queried them when I needed such as the documents for the latest conversations and the event list. Furthermore, as I mentioned before about implementing the recycler view for my UI, I was also required to bind my data with each of the view objects. I implicitly built some adapters for some of my models such as users and events. These adapters helped me integrate my objects with views with the help of the view holders. As a result, I had a more readable source code and less execution time in comparison to some traditional ways of implementing the view objects with the data-bound to them. Thank you for reading!



For Full application video please check out the demo video folder for final video record full.

