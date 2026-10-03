"""Original synthetic comic fixture for Chika-eInk screenshots. MPL-2.0."""
from pathlib import Path
from io import BytesIO
from zipfile import ZipFile, ZIP_DEFLATED
from PIL import Image, ImageDraw, ImageFont

import argparse

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('output', type=Path, help='Directory for the generated CBZ')
root = parser.parse_args().output
root.mkdir(parents=True, exist_ok=True)
fonts = Path(__file__).resolve().parents[1] / 'app/src/main/res/font'
regular = ImageFont.truetype(str(fonts / 'libron_regular.ttf'), 36)
bold = ImageFont.truetype(str(fonts / 'libron_bold.ttf'), 64)


def speech(draw, box, text):
    draw.rounded_rectangle(box, radius=20, fill='white', outline='black', width=5)
    draw.text((box[0]+24, box[1]+20), text, fill='black', font=regular)


def shelf(draw, x, y, count=8):
    for i in range(count):
        left=x+i*50
        draw.rectangle((left,y,left+38,y+180-(i%3)*20),fill='white',outline='black',width=5)
        draw.line((left+8,y+25,left+28,y+25),fill='black',width=3)
    draw.rectangle((x-15,y+180,x+count*50,y+198),fill='black')


def reader(draw, x, y):
    draw.ellipse((x-32,y-32,x+32,y+32),outline='black',width=6)
    draw.line((x,y+32,x,y+175),fill='black',width=8)
    draw.line((x,y+175,x-60,y+255),fill='black',width=8)
    draw.line((x,y+175,x+60,y+255),fill='black',width=8)
    draw.line((x,y+65,x-70,y+105),fill='black',width=8)
    draw.line((x,y+65,x+70,y+105),fill='black',width=8)
    draw.polygon([(x-85,y+70),(x,y+85),(x,y+170),(x-85,y+155)],fill='white',outline='black',width=5)
    draw.polygon([(x,y+85),(x+85,y+70),(x+85,y+155),(x,y+170)],fill='white',outline='black',width=5)


pages=[]
for page in range(3):
    img=Image.new('RGB',(1200,1800),'white')
    d=ImageDraw.Draw(img)
    d.text((70,35),'THE QUIET LIBRARY',font=bold,fill='black')
    d.text((70,115),f'Original synthetic demo · page {page+1}',font=regular,fill='black')
    boxes=[(60,200,1140,640),(60,690,570,1180),(630,690,1140,1180),(60,1230,1140,1740)]
    for box in boxes:d.rectangle(box,outline='black',width=10)
    speech(d,(100,235,640,325),'A quiet place to read.')
    shelf(d,130,395,10)
    reader(d,930,355)
    speech(d,(85,725,540,825),'One panel at a time.')
    reader(d,310,905)
    speech(d,(660,725,1110,825),'No hurry. Just a tap.')
    shelf(d,700,900,7)
    speech(d,(100,1270,660,1370),'A whole page of possibilities.')
    shelf(d,130,1480,9)
    reader(d,945,1410)
    stream=BytesIO();img.save(stream,format='PNG');pages.append(stream.getvalue())
with ZipFile(root/'Quiet Library Demo.cbz','w',ZIP_DEFLATED) as archive:
    for i,data in enumerate(pages):archive.writestr(f'{i+1:03}.png',data)
print(root/'Quiet Library Demo.cbz')
