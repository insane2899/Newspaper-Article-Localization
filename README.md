# Newspaper-Article-Localization

This is a project made in Java 11. The main objective of the project was to create a programme that will remove and separate all the articles of a newspaper image.
The second part of the project was to segregate the separated articles according to their contents. 


To run this project all one needs to do is have Java 11 compiler installed in their local machine. The sample input images are given in the datasets folder. One can 
use other pictures as one needs just needs to write the path to the image in the programme main method.


The programme separates the image along the white spaces between the articles. For example if the sample input is:

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/input.png?raw=true "Sample Input")

Then the first thing the project does is convert the image in a grayscale image. Then it turns the grayscale image into a binarized image. Right now the binarization process has only two techniques. Manual thresholding and Otsu's Thresholding. Both of them are used in global scale. Hopefully I will be able to add some new and better techniques of binarization of images in the future. The sample binarized output is:

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/BW.jpg?raw=true "Binarized Image")

Now the programme checks for horizontal lines where all the pixels have intensity 255 (i.e white). If it finds such a row it just stores the row numbers in an array. The programme finds such consecutive rows it skips these rows. The stored numbers are saved in the array.

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/1.jpg?raw=true "First Segment")

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/2.jpg?raw=true "Second Segment")

Then the programme checks for vertical lines in each of the created segments, where the pixel intensity is 255 (i.e white). If it finds such a column it stores the column numbers in an array.

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/2a.jpg?raw=true "First Subsegment")

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/2b.jpg?raw=true "Second Subsegment")

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/2c.jpg?raw=true "Third Subsegment")

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/2d.jpg?raw=true "Fourth Subsegment")

All the segments shown are separated from the binarized image by using the numbers stored in the arrays. The row array contains the row numbers and the column array contains the column numbers. So from two such numbers we get a rectangle, i.e top left position and bottom right position numbers will give a rectangle. The pixels between these rectangles are copied and kept in a BufferedImage instance and they are then put into a file to get all the segmented articles.

Result:

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/2-1Result.jpg?raw=true "Result Segment")

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/3-2Result.jpg?raw=true "Result Segment")

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/3-3Result.jpg?raw=true "Result Segment")

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/3-4Result.jpg?raw=true "Result Segment")

![Screenshot](https://github.com/insane2899/Newspaper-Article-Localization/blob/master/Images/3-5Result.jpg?raw=true "Result Segment")

The project has a lot of shortcommings.
<ul>
  <li>The first point is the fact that the binarization process is poor right now. Atleast a local binarization technique has to be used to get the proper binarized result.</li>
  <li>The image must be straight. A crooked or a skewed image will give no proper straight line with all white pixels and as a result will give no segments.</li>
  <li>If the subsections are nested too much then the segmentation will not occur. The example has the segment 2 i.e the second segment with the image. The two articles below the image were not segmented because they were inside a block of segment. So unless one recursively keeps doing the same segmentation one will fail to reach the most atomic level articles.</li>
 </ul>
 
 The only solutions I could think of for the above problems are:
 <ul>
  <li>Use more binarization techniques like Sauvola or Niblack Technique for better binarization and noise removal</li>
  <li>To use some skew correction algorithm on the image before the binarization step. I tried to use the skew correction algorithm used by CamScanner android application but was unable to hook it with my programme.</li>
  <li>Instead of using straight lines to find the segments I thought of first darkening the white pixels in close proximity with two dark pixels. In this way we can remove the spaces between the words or lines of an article. I wrote two methods named RLSA_H and RLSA_V  to implement this process. Once these methods are run all the small article blocks will be darkened. Then I thought of somehow extracting the left top and the right bottom positions of these dark blocks of the newspaper and copy the pixels inside these rectangles. But i was unable to find some way to do the same.</li>
 </ul>
 
 Finally I would like to notify everyone that this is a public project. So anyone who wants to use the algorithms for his/her projects feel free to use it. Also if anyone has any better idea about improving the above programme please feel free to contact me. My email is imrimosen@gmail.com . In short you can do anything with my code as long as it does not cause any harm to anyone else. XD
  


