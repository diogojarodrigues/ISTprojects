from PIL import Image

# Load the image
image = Image.open('tagus.png')
pixels = list(image.getdata())
width, height = image.size

LIMIT = 1050 

new_pixels = []
for i in range(LIMIT):
	for j in range(i +1):
		pixel = pixels[(j*width) + (i-j)]
		new_pixel = []
		
		for channel in pixel:
			lsb = channel & 0x1F
			new_pixel.append(lsb)

		new_pixels.append((new_pixel[0],new_pixel[1],new_pixel[2]))

# lsb pixels in a new image
new_image = Image.new('RGB', (LIMIT, LIMIT))
new_image.putdata(new_pixels)
new_image.save('lsb_extracted_image.png')
