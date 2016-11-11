#!/usr/bin/env python3

from PyQt5.QtWidgets import QLineEdit
from PyQt5.QtGui import QKeySequence

class TypeOnlyLineEdit(QLineEdit):
    def __init(self, parent):
        QLineEdit.__init__(self, parent)
    
    def keyPressEvent(self, event):
        if event.matches(QKeySequence.Copy) or event.matches(QKeySequence.Cut) or event.matches(QKeySequence.Paste):
            event.ignore()
        else:
            return QLineEdit.keyPressEvent(self, event)