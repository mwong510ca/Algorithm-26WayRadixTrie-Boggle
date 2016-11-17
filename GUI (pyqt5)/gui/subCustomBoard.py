# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'customBoggle.ui'
#
# Created by: PyQt5 UI code generator 5.6
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import *

from gui.subBoardWindow import Ui_MainWindow

class BoardWidget(QWidget, Ui_MainWindow):
    boardSize = QtCore.pyqtSignal(int)
    letterAdded = QtCore.pyqtSignal(int)
    boardReady = QtCore.pyqtSignal()
    
    def __init__(self):
        super().__init__()
        self.setupUi(self)
        self.default_setting()
        print("correct")

    def default_setting(self):
        self.letters_set = [[self.letter00, self.letter01, self.letter02, self.letter03, self.letter04, self.letter05],
                [self.letter10, self.letter11, self.letter12, self.letter13, self.letter14, self.letter15],
                [self.letter20, self.letter21, self.letter22, self.letter23, self.letter24, self.letter25],
                [self.letter30, self.letter31, self.letter32, self.letter33, self.letter34, self.letter35],
                [self.letter40, self.letter41, self.letter42, self.letter43, self.letter44, self.letter45],
                [self.letter50, self.letter51, self.letter52, self.letter53, self.letter54, self.letter55]]
        
        letters_choice = {0  : "#", 1  : "A",  2 : "B",  3 : "C",  4 : "D",  5 : "E",  
                6  : "F", 7  : "G", 8  : "H",  9 : "I", 10 : "J", 11 : "K", 12 : "L", 
                13 : "M", 14 : "N", 15 : "O", 16 : "P", 17 :"Qu", 18 : "R", 19 : "S", 
                20 : "T", 21 : "U", 22 : "V", 23 : "W", 24 : "X", 25 : "Y", 26 : "Z", 
                27 :"An", 28 :"Er", 29 :"He", 30 :"In", 31:"Th"}

        self.code_restore = {27 : 101, 28 : 105, 29 : 108, 30 : 109, 31 : 120}
        
        for row in range(6):
            for col in range (6):
                self.letters_set[row][col].setMinimumSize(0, 0)
                for idx in range(32):
                    self.letters_set[row][col].addItem(letters_choice[idx])
       
        self.sizeGroup.buttonClicked.connect(self.change_size)
        self.submitButton.clicked.connect(self.board_ready)
        self.cancelButton.clicked.connect(self.hide)
        
    def board_reset(self):
        for row in range(6):
            for col in range (6):
                self.letters_set[row][col].setCurrentIndex((row * 6 + col + 1) % 32) 

    def select_size(self, size):
        self.size_in_use = size
        if size == 4:
            self.size4Button.setChecked(True)
        elif size == 5:
            self.size5Button.setChecked(True)
        elif size == 6:
            self.size6Button.setChecked(True)
        self.board_resize()

    def change_size(self):
        if self.size4Button.isChecked():
            self.size_in_use = 4
        elif self.size5Button.isChecked():
            self.size_in_use = 5
        else:
            self.size_in_use = 6
        self.board_resize()

    def board_resize(self):
        width = 600 / self.size_in_use
        for row in range(6):
            if row < self.size_in_use:
                for col in range(6):
                    if col < self.size_in_use:
                        self.letters_set[row][col].setMaximumSize(width, width)
                    else:
                        self.letters_set[row][col].setMaximumSize(0, width)
            else:
                for col in range(6):
                    if col < self.size_in_use:
                        self.letters_set[row][col].setMaximumSize(width, 0)
                    else:
                        self.letters_set[row][col].setMaximumSize(0, 0)

    def board_ready(self):
        self.boardSize.emit(self.size_in_use)
        for row in range(self.size_in_use):
            for col in range(self.size_in_use):
                idx = self.letters_set[row][col].currentIndex()
                if idx < 27:
                    self.letterAdded.emit(idx)
                else:
                    self.letterAdded.emit(self.code_restore[idx])
        self.boardReady.emit()




