"""
" TypeOnlyLineEdit is the custom QLineEdit object of input field for appBoggle.
" It disabled copy and paste feature and force the user to type in only.
"
" author Meisze Wong
"        www.linkedin.com/pub/macy-wong/46/550/37b/
"        github.com/mwong510ca/Boggle_TrieDataStructure
"""

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