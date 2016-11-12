# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'listWindow.ui'
#
# Created by: PyQt5 UI code generator 5.6
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import QWidget

class Ui_MainWindow(QWidget):
    def __init__(self, words):
        super().__init__()
        self._words = words
        self.initUI()

    def initUI(self):
        self.setObjectName("Form")
        self.resize(200, 400)
        self.setMinimumSize(QtCore.QSize(200, 400))
        self.setMaximumSize(QtCore.QSize(300, 16777215))

        self.gridLayout = QtWidgets.QGridLayout(self)
        self.gridLayout.setObjectName("gridLayout")
        self.textBrowser = QtWidgets.QTextBrowser(self)
        sizePolicy = QtWidgets.QSizePolicy(QtWidgets.QSizePolicy.Preferred, QtWidgets.QSizePolicy.Preferred)
        sizePolicy.setHorizontalStretch(0)
        sizePolicy.setVerticalStretch(0)
        sizePolicy.setHeightForWidth(self.textBrowser.sizePolicy().hasHeightForWidth())
        self.textBrowser.setSizePolicy(sizePolicy)
        self.textBrowser.setObjectName("textBrowser")
        self.textBrowser.setMaximumSize(QtCore.QSize(300, 16777215))
        self.gridLayout.addWidget(self.textBrowser, 1, 0, 1, 1)
        self.retranslateUi()
        self.show()
        
    def retranslateUi(self):
        _translate = QtCore.QCoreApplication.translate
        self.setWindowTitle("Cheat Sheet")
        self.textBrowser.setText(_translate("Form", self._words))
        

