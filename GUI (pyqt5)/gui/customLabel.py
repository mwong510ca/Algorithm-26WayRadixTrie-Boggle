"""
" ImageLabel is the custom QLabel object of letters images for appBoggle.
" It supported click and mouseover function for the letter image.
"
" author Meisze Wong
"        www.linkedin.com/pub/macy-wong/46/550/37b/
"        github.com/mwong510ca/Boggle_TrieDataStructure
"""

#!/usr/bin/env python3

from PyQt5.QtWidgets import *
from PyQt5.QtCore import pyqtSignal


class ImageLabel(QLabel):
    clickedLabel = pyqtSignal()
    enteredLabel = pyqtSignal()

    def __init(self, parent):
        QLabel.__init__(self, parent)
        
    def setPosition(self, row, col):
        self._row = row
        self._col = col
     
    def mousePressEvent(self, ev):
        self.clickedLabel.emit()

    def enterEvent(self, event):
        self.enteredLabel.emit()

    def getRow(self):
        return self._row

    def getCol(self):
        return self._col
