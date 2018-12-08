from pathlib import Path

input_path = Path("/local/tmp/happy_holidays1/anaglyph/")
output_path = Path("/local/tmp/happy_holidays1/anaglyph_reversed/")

if not output_path.exists():
    output_path.mkdir()

images = sorted(list(input_path.glob("happy_holidays*")))
rev_images = sorted([output_path.joinpath(f.name)
                     for f in images], reverse=True)

for img, rev_img in zip(images, rev_images):
    print(rev_img, img)
    rev_img.symlink_to(img)
