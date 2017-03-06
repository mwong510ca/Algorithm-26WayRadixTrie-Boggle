"""
" BoggleImages is the QObject to load the images for main application
" appBoggle.
"
" author Meisze Wong
"        www.linkedin.com/pub/macy-wong/46/550/37b/
"        github.com/mwong510ca/Boggle_TrieDataStructure
"""

# !/usr/bin/env python3

import sys

from PyQt5.QtCore import QObject, QDir, QFile
from PIL import Image

IMG_FOLDER_NAME = "images"


class BoggleImages(QObject):
    def __init__(self, letter_map):
        super(BoggleImages, self).__init__()
        load_success = False
        self.images_map = {}

        dir_path = QDir()
        if dir_path.exists(IMG_FOLDER_NAME):
            load_success = True
            prefix = IMG_FOLDER_NAME + QDir.separator() + "letter_"
            for code, ch in letter_map.items():
                file_path = prefix + ch + ".png"
                if not QFile(file_path).exists():
                    load_success = False
                    break
                try:
                    Image.open(file_path)
                    self.images_map[code] = file_path
                except IOError:
                    load_success = False
                    break

                file_path = prefix + ch + "+.png"
                if not QFile(file_path).exists():
                    load_success = False
                    break
                try:
                    Image.open(file_path)
                    self.images_map[code + 50] = file_path
                except IOError:
                    load_success = False
                    break

            extra = {0: "letter_#", 200: "space_right", 201: "link_right",
                     300: "space_down", 301: "link_down", 400: "space_cross",
                     401: "link_lowerR", 402: "link_upperR", 403: "link_cross"}
            prefix = IMG_FOLDER_NAME + dir_path.separator()
            for code, filename in extra.items():
                file_path = prefix + filename + ".png"
                if not QFile(file_path).exists():
                    load_success = False
                    break
                try:
                    Image.open(file_path)
                    self.images_map[code] = file_path
                except IOError:
                    load_success = False
                    break
        if not load_success:
            print("Missing required tile images, exit program.")
            sys.exit()

    def getPath(self, code):
        return self.images_map[code]