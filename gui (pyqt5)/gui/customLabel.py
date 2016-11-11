#!/usr/bin/env python3

from PyQt5.QtWidgets import *
from PyQt5.QtCore import pyqtSignal

class ImageLabel(QLabel):
    clickedLabel = pyqtSignal()
    enteredLabel = pyqtSignal()
    #leftLabel = pyqtSignal(bool)
    
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
