# -*- coding: utf-8 -*-
# !/usr/bin/env python3

import sys
import os
import math
import time
import subprocess
import socket

from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import QApplication, QMainWindow, QFrame, QMessageBox, \
        QWidget, QFileDialog, QInputDialog
from PyQt5.QtCore import QDir, QFile, QObject, pyqtSignal, QThread, QRect
from PyQt5.QtGui import QPixmap
from PIL import Image

from gui.mainWindow import Ui_MainWindow as MainWindow
from gui.subCheatSheet import Ui_MainWindow as ListingWindow
from gui.subCustomBoard import BoardWidget
from utilities.gameTimer import Timer
from py4j.java_gateway import JavaGateway
from py4j.java_gateway import GatewayClient

# Globals
IMG_FOLDER_NAME = "images"
DICE_4_SIZE = 256
LINK_SIZE = 16

class GameBoggle(QMainWindow, MainWindow):
    closing = QtCore.pyqtSignal()

    def __init__(self, gateway):
        super().__init__()
        self._gateway = gateway
        self.setupUi(self)
        self.default_setting()

    def default_setting(self):
        self.letter00.clickedLabel.connect(lambda: self.clicked_letter(0, 0))
        self.letter01.clickedLabel.connect(lambda: self.clicked_letter(0, 1))
        self.letter02.clickedLabel.connect(lambda: self.clicked_letter(0, 2))
        self.letter03.clickedLabel.connect(lambda: self.clicked_letter(0, 3))
        self.letter04.clickedLabel.connect(lambda: self.clicked_letter(0, 4))
        self.letter05.clickedLabel.connect(lambda: self.clicked_letter(0, 5))
        self.letter10.clickedLabel.connect(lambda: self.clicked_letter(1, 0))
        self.letter11.clickedLabel.connect(lambda: self.clicked_letter(1, 1))
        self.letter12.clickedLabel.connect(lambda: self.clicked_letter(1, 2))
        self.letter13.clickedLabel.connect(lambda: self.clicked_letter(1, 3))
        self.letter14.clickedLabel.connect(lambda: self.clicked_letter(1, 4))
        self.letter15.clickedLabel.connect(lambda: self.clicked_letter(1, 5))
        self.letter20.clickedLabel.connect(lambda: self.clicked_letter(2, 0))
        self.letter21.clickedLabel.connect(lambda: self.clicked_letter(2, 1))
        self.letter22.clickedLabel.connect(lambda: self.clicked_letter(2, 2))
        self.letter23.clickedLabel.connect(lambda: self.clicked_letter(2, 3))
        self.letter24.clickedLabel.connect(lambda: self.clicked_letter(2, 4))
        self.letter25.clickedLabel.connect(lambda: self.clicked_letter(2, 5))
        self.letter30.clickedLabel.connect(lambda: self.clicked_letter(3, 0))
        self.letter31.clickedLabel.connect(lambda: self.clicked_letter(3, 1))
        self.letter32.clickedLabel.connect(lambda: self.clicked_letter(3, 2))
        self.letter33.clickedLabel.connect(lambda: self.clicked_letter(3, 3))
        self.letter34.clickedLabel.connect(lambda: self.clicked_letter(3, 4))
        self.letter35.clickedLabel.connect(lambda: self.clicked_letter(3, 5))
        self.letter40.clickedLabel.connect(lambda: self.clicked_letter(4, 0))
        self.letter41.clickedLabel.connect(lambda: self.clicked_letter(4, 1))
        self.letter42.clickedLabel.connect(lambda: self.clicked_letter(4, 2))
        self.letter43.clickedLabel.connect(lambda: self.clicked_letter(4, 3))
        self.letter44.clickedLabel.connect(lambda: self.clicked_letter(4, 4))
        self.letter45.clickedLabel.connect(lambda: self.clicked_letter(4, 5))
        self.letter50.clickedLabel.connect(lambda: self.clicked_letter(5, 0))
        self.letter51.clickedLabel.connect(lambda: self.clicked_letter(5, 1))
        self.letter52.clickedLabel.connect(lambda: self.clicked_letter(5, 2))
        self.letter53.clickedLabel.connect(lambda: self.clicked_letter(5, 3))
        self.letter54.clickedLabel.connect(lambda: self.clicked_letter(5, 4))
        self.letter55.clickedLabel.connect(lambda: self.clicked_letter(5, 5))
        
        self.letter00.enteredLabel.connect(lambda: self.linked_letter(0, 0))
        self.letter01.enteredLabel.connect(lambda: self.linked_letter(0, 1))
        self.letter02.enteredLabel.connect(lambda: self.linked_letter(0, 2))
        self.letter03.enteredLabel.connect(lambda: self.linked_letter(0, 3))
        self.letter04.enteredLabel.connect(lambda: self.linked_letter(0, 4))
        self.letter05.enteredLabel.connect(lambda: self.linked_letter(0, 5))
        self.letter10.enteredLabel.connect(lambda: self.linked_letter(1, 0))
        self.letter11.enteredLabel.connect(lambda: self.linked_letter(1, 1))
        self.letter12.enteredLabel.connect(lambda: self.linked_letter(1, 2))
        self.letter13.enteredLabel.connect(lambda: self.linked_letter(1, 3))
        self.letter14.enteredLabel.connect(lambda: self.linked_letter(1, 4))
        self.letter15.enteredLabel.connect(lambda: self.linked_letter(1, 5))
        self.letter20.enteredLabel.connect(lambda: self.linked_letter(2, 0))
        self.letter21.enteredLabel.connect(lambda: self.linked_letter(2, 1))
        self.letter22.enteredLabel.connect(lambda: self.linked_letter(2, 2))
        self.letter23.enteredLabel.connect(lambda: self.linked_letter(2, 3))
        self.letter24.enteredLabel.connect(lambda: self.linked_letter(2, 4))
        self.letter25.enteredLabel.connect(lambda: self.linked_letter(2, 5))
        self.letter30.enteredLabel.connect(lambda: self.linked_letter(3, 0))
        self.letter31.enteredLabel.connect(lambda: self.linked_letter(3, 1))
        self.letter32.enteredLabel.connect(lambda: self.linked_letter(3, 2))
        self.letter33.enteredLabel.connect(lambda: self.linked_letter(3, 3))
        self.letter34.enteredLabel.connect(lambda: self.linked_letter(3, 4))
        self.letter35.enteredLabel.connect(lambda: self.linked_letter(3, 5))
        self.letter40.enteredLabel.connect(lambda: self.linked_letter(4, 0))
        self.letter41.enteredLabel.connect(lambda: self.linked_letter(4, 1))
        self.letter42.enteredLabel.connect(lambda: self.linked_letter(4, 2))
        self.letter43.enteredLabel.connect(lambda: self.linked_letter(4, 3))
        self.letter44.enteredLabel.connect(lambda: self.linked_letter(4, 4))
        self.letter45.enteredLabel.connect(lambda: self.linked_letter(4, 5))
        self.letter50.enteredLabel.connect(lambda: self.linked_letter(5, 0))
        self.letter51.enteredLabel.connect(lambda: self.linked_letter(5, 1))
        self.letter52.enteredLabel.connect(lambda: self.linked_letter(5, 2))
        self.letter53.enteredLabel.connect(lambda: self.linked_letter(5, 3))
        self.letter54.enteredLabel.connect(lambda: self.linked_letter(5, 4))
        self.letter55.enteredLabel.connect(lambda: self.linked_letter(5, 5))
        
        self.dice_faces = [[self.letter00, self.letter01, self.letter02, self.letter03, self.letter04, self.letter05],
                [self.letter10, self.letter11, self.letter12, self.letter13, self.letter14, self.letter15],
                [self.letter20, self.letter21, self.letter22, self.letter23, self.letter24, self.letter25],
                [self.letter30, self.letter31, self.letter32, self.letter33, self.letter34, self.letter35],
                [self.letter40, self.letter41, self.letter42, self.letter43, self.letter44, self.letter45],
                [self.letter50, self.letter51, self.letter52, self.letter53, self.letter54, self.letter55]]

        self.dice_linkh = [[self.linkh00, self.linkh01, self.linkh02, self.linkh03, self.linkh04],
                [self.linkh10, self.linkh11, self.linkh12, self.linkh13, self.linkh14],
                [self.linkh20, self.linkh21, self.linkh22, self.linkh23, self.linkh24],
                [self.linkh30, self.linkh31, self.linkh32, self.linkh33, self.linkh34],
                [self.linkh40, self.linkh41, self.linkh42, self.linkh43, self.linkh44],
                [self.linkh50, self.linkh51, self.linkh52, self.linkh53, self.linkh54]]

        self.dice_linkv = [[self.linkv00, self.linkv01, self.linkv02, self.linkv03, self.linkv04, self.linkv05],
                [self.linkv10, self.linkv11, self.linkv12, self.linkv13, self.linkv14, self.linkv15],
                [self.linkv20, self.linkv21, self.linkv22, self.linkv23, self.linkv24, self.linkv25],
                [self.linkv30, self.linkv31, self.linkv32, self.linkv33, self.linkv34, self.linkv35],
                [self.linkv40, self.linkv41, self.linkv42, self.linkv43, self.linkv44, self.linkv45]]

        self.dice_linkx = [[self.linkx00, self.linkx01, self.linkx02, self.linkx03, self.linkx04],
                [self.linkx10, self.linkx11, self.linkx12, self.linkx13, self.linkx14],
                [self.linkx20, self.linkx21, self.linkx22, self.linkx23, self.linkx24],
                [self.linkx30, self.linkx31, self.linkx32, self.linkx33, self.linkx34],
                [self.linkx40, self.linkx41, self.linkx42, self.linkx43, self.linkx44]]
        
        self.face_encode = {"A": 1, "B" :  2, "C" :  3, "D" :  4, "E" :  5, "F" :  6, 
                "G" :  7, "H" :  8, "I" :  9, "J" : 10, "K" : 11, "L" : 12, "M" : 13, 
                "N" : 14, "O" : 15, "P" : 16, "QU": 17, "R" : 18, "S" : 19, "T" : 20, 
                "U" : 21, "V" : 22, "W" : 23, "X" : 24, "Y" : 25, "Z" : 26, 
                "AN":101, "ER":105, "HE":108, "IN":109, "TH":120}

        self.face_decode = {1: "A",  2 : "B",  3 : "C",  4 : "D",  5 : "E",  6 : "F", 
                7  : "G", 8  : "H",  9 : "I", 10 : "J", 11 : "K", 12 : "L", 13 : "M", 
                14 : "N", 15 : "O", 16 : "P", 17 :"QU", 18 : "R", 19 : "S", 20 : "T", 
                21 : "U", 22 : "V", 23 : "W", 24 : "X", 25 : "Y", 26 : "Z", 
                101:"AN", 105:"ER", 108:"HE", 109:"IN", 120:"TH"}

        self.score_table = {3 : 1, 4 : 1, 5 : 2, 6 : 3, 7 : 5}

        self.load_images()
        
        # size, generate function, game time, score function, min length
        self._board_types = {0 : [4, "Boggle (1992)", self._gateway.getNew1992Board, 3, self.score_3, 3],
           1 : [4, "Classic Boggle", self._gateway.getClassicBoard, 3, self.score_3, 3], 
           2 : [5, "Boggle Deluxe", self._gateway.getDeluxeBoard, 3, self.score_3, 3],
           3 : [5, "Big Boggle (1979)", self._gateway.getBigBoard, 3, self.score_3, 3],
           4 : [6, "Super Big Boggle", self._gateway.getSuperBigBoard, 4, self.score_4, 4]
         }

        self.trace_history = []
        self.game_thread = Timer()
        self.game_thread.stop()
        self.list_window = 0
        self.boggle_setting(1) 
        self.board_window = BoardWidget()
        self.board_window.boardSize.connect(self.custom_size)
        self.board_window.letterAdded.connect(self.custom_letter_append)
        self.board_window.boardReady.connect(self.custom_ready)
        
        self.gameNew.clicked.connect(self.game_start) 
        self.game_thread.currentTime.connect(self.gameTime.setText) 
        self.game_thread.timesUp.connect(self.game_terminate) 
        self.playerInput.textChanged.connect(self.input_changed)
        self.playerInput.editingFinished.connect(lambda: self.input_submitted(self.playerInput))
        
        self.actionExit.triggered.connect(self.custom_quit)
        self.actionNew_4x4.triggered.connect(lambda: self.boggle_setting(0)) 
        self.actionClassic_4x4.triggered.connect(lambda: self.boggle_setting(1)) 
        self.actionDeluex_5x5.triggered.connect(lambda: self.boggle_setting(2)) 
        self.actionBig_5x5_1979.triggered.connect(lambda: self.boggle_setting(3)) 
        self.actionSuperBig_6x6.triggered.connect(lambda: self.boggle_setting(4)) 
        self.actionCustomBoard.triggered.connect(self.custom_board) 

        self.actionOSPD_US.triggered.connect(self.dictionary_ospd) 
        self.actionEOWL_UK.triggered.connect(self.dictionary_eowl) 
        self.actionSOWPODS.triggered.connect(self.dictionary_sowpods) 
        self.actionCustomDictionary.triggered.connect(self.dictionary_custom) 
        self.actionInstructions.triggered.connect(self.popup_instructions)
        self.actionCheatSheet.triggered.connect(self.cheat_sheet)    
        self.actionAboutAuthor.triggered.connect(self.about_author) 
        
    def load_images(self):
        self.image_mode = True
        temp_map = {}

        dir = QtCore.QDir()
        if dir.exists(IMG_FOLDER_NAME):  
            prefix = IMG_FOLDER_NAME + QDir.separator() + "letter_" 
            for code, ch in self.face_decode.items():
                filepath = prefix + ch + ".png"
                if not QFile(filepath).exists():
                    self.image_mode = False
                    break
                try:
                    img = Image.open(filepath)
                    temp_map[code] = filepath
                except IOError:
                    self.image_mode = False
                    break

                filepath = prefix + ch + "+.png"
                if not QFile(filepath).exists():
                    self.image_mode = False
                    break
                try:
                    img = Image.open(filepath)
                    temp_map[code + 50] = filepath
                except IOError:
                    self.image_mode = False
                    break

            extra = {0 : "letter_#", 200 : "space_right", 201 : "link_right", 
                    300 : "space_down", 301 : "link_down", 400 : "space_cross",
                    401 : "link_lowerR", 402 : "link_upperR", 403 : "link_cross"}        
            prefix = IMG_FOLDER_NAME + QDir.separator()
            for code, filename in extra.items():
                filepath = prefix + filename + ".png"
                if not QFile(filepath).exists():
                    self.image_mode = False
                    break
                try:
                    img = Image.open(filepath)
                    temp_map[code] = filepath
                except IOError:
                    self.image_mode = False
                    break
        if self.image_mode:
            self.image_list = temp_map
        else:
            sys.exit()

    def boggle_resize(self, boggle_size):        
        self.actionCheatSheet.setEnabled(False)
        self.playerInput.setEnabled(False)
        self.gameIntro.setText("Change menu setting or start the game.")
        if self.list_window:
            try:
                self.list_window.close()
            except:
                pass

        size = ((DICE_4_SIZE * 4) - (LINK_SIZE * (boggle_size - 4))) // boggle_size
        for row in range(6):
            if row < boggle_size:
                for col in range(6):
                    if col < boggle_size:
                        self.dice_faces[row][col].setMaximumSize(size, size)
                    else:
                        self.dice_faces[row][col].setMaximumSize(0, size)
            else:
                for col in range(6):
                    if col < boggle_size:
                        self.dice_faces[row][col].setMaximumSize(size, 0)
                    else:
                        self.dice_faces[row][col].setMaximumSize(0, 0)
        
        link_size = boggle_size - 1
        for col in range(5):
            if col < link_size:
                self.dice_linkh[0][col].setMaximumSize(LINK_SIZE, size)
                self.dice_linkv[col][0].setMaximumSize(size, LINK_SIZE)
            else:
                self.dice_linkh[0][col].setMaximumSize(0, size)
                self.dice_linkv[col][0].setMaximumSize(size, 0)
        
        for row in range(5):
            if row < link_size:
                for col in range(5):
                    if col < link_size:
                        self.dice_linkh[row + 1][col].setMaximumSize(LINK_SIZE, size)
                        self.dice_linkv[col][row + 1].setMaximumSize(size, LINK_SIZE)
                        self.dice_linkx[row][col].setMinimumSize(LINK_SIZE, LINK_SIZE)
                        self.dice_linkx[row][col].setMaximumSize(LINK_SIZE, LINK_SIZE)
                    else:
                        self.dice_linkh[row + 1][col].setMaximumSize(0, size)
                        self.dice_linkv[col][row + 1].setMaximumSize(size, 0)
                        self.dice_linkx[row][col].setMinimumSize(0, 0)
                        self.dice_linkx[row][col].setMaximumSize(0, 0)
            else:
                for col in range(5):
                    if col < link_size:
                        self.dice_linkh[row + 1][col].setMaximumSize(0, size)
                        self.dice_linkv[col][row + 1].setMaximumSize(size, 0)
                        self.dice_linkx[row][col].setMinimumSize(0, 0)
                        self.dice_linkx[row][col].setMaximumSize(0, 0)
                    else:
                        self.dice_linkh[row + 1][col].setMaximumSize(0, 0)
                        self.dice_linkv[col][row + 1].setMaximumSize(0, 0)
                        self.dice_linkx[row][col].setMinimumSize(0, 0)
                        self.dice_linkx[row][col].setMaximumSize(0, 0)

    def boggle_setting(self, option):
        self.boggle_size = self._board_types[option][0]
        self.gameNew.setText("New " + self._board_types[option][1])
        self.boggle_create = self._board_types[option][2]
        self.boggle_time = self._board_types[option][3]
        self.boggle_scoring = self._board_types[option][4]
        self.boggle_min_length = self._board_types[option][5]
        self.boggle_resize(self.boggle_size)

        if self.game_thread.isRunning():
            self.game_thread.stop()
            self.game_terminate()
        self.gameTime.setText("Time:")
        self.gameScores.setText("Scores:")
        self.game_active = False

    def game_start(self):
        if self.gameNew.text() == "New Custom Boggle Board":
            self.custom_board()
            return
        self.game_board = self.boggle_create()
        self.game_load()

    def game_load(self):
        self.gameIntro.setText("Type the word or click the first and last letter.")
        self.actionCheatSheet.setEnabled(True)
        self.playerInput.setEnabled(True)
        if self.list_window:
            try:
                self.list_window.close()
            except:
                pass

        self.boggle_words = self._gateway.getBoggleWords()
        self.max_scores = self._gateway.getMaxScores()
        codes = self.game_board.guiCode()
        self.face_code = {}
        self.face_lookup = {}
        self.word_trace = False
        self.word_list = []
        self.word_history = ""
        self.game_scores = 0
        self.gameScores.setText("Scores: " + str(self.game_scores) + " out of " + str(self.max_scores))
        self.wordListDisplay.setText("")
        idx = 0
        for row in range(self.boggle_size):
            for col in range(self.boggle_size):
                code = codes[idx]
                if code > 0:
                    self.face_code[idx] = code
                    phase = self.face_decode[code]
                    if self.face_lookup.get(phase):
                        temp = self.face_lookup[phase]
                        temp.append(idx)
                        self.face_lookup[phase] = temp
                    else:
                        self.face_lookup[phase] = [idx]
                self.dice_faces[row][col].setPixmap(QPixmap(self.image_list[code]))
                idx += 1
        if self.max_scores > 0:
            self.game_thread.setTimer(self.boggle_time)
            self.game_thread.start()
            self.game_active = True
        else:
            QMessageBox.information(None, 'No word in this board. Game won\'t start.\n' +
                'Please generate another board or change dictionary.', 
                QMessageBox.Close, QMessageBox.Close)

    def game_terminate(self):
        self.game_active = False
        self.gameTime.setText("Time: 0.0s")
        self.playerInput.setEnabled(False)
        for idx2 in self.trace_history:
            row2 = idx2 // self.boggle_size
            col2 = idx2 % self.boggle_size
            self.dice_faces[row2][col2].setPixmap(QPixmap(
                self.image_list[self.face_code.get(idx2)]))
        self.gameIntro.setText("Time's up.  Try another game.")

    def clicked_letter(self, row, col):
        if self.game_active:
            idx = row * self.boggle_size + col
            if self.face_code.get(idx):
                if self.word_trace:
                    word = ""
                    for idx in self.trace_history:
                        word = word + self.face_decode[self.face_code.get(idx)]
                    score = self.boggle_scoring(word)
                    if score > 0 and not word in self.word_list:
                        self.game_scores += score
                        self.gameScores.setText("Scores: " + str(self.game_scores) + " out of " + str(self.max_scores))
                        if len(self.word_history) > 0:
                            self.word_history = "\n" + self.word_history
                        self.word_history = word + " (" + str(score) + ")" + self.word_history
                        self.wordListDisplay.setText(self.word_history)
                        self.word_list.append(word)
                        if self.max_scores == self.game_scores:
                            self.game_thread.stop()
                            self.game_active = False
                            self.playerInput.setEnabled(False)
                            self.gameIntro.setText("You found all words.  Early termination.")

                    self.clear_history()
                    self.playerInput.setText("") 
                else:
                    self.word_trace = True
                    self.trace_history = []
                    self.link_history = []
                    self.link_cross_history = []
                    idx = row * self.boggle_size + col
                    if self.face_code.get(idx):
                        self.trace_history.append(idx)
                        self.dice_faces[row][col].setPixmap(QPixmap(
                            self.image_list[self.face_code.get(idx) + 50]))
                        self.playerInput.setText(self.face_decode[self.face_code.get(idx)])
                    
    def linked_letter(self, row, col):
        if self.game_active:
            idx = row * self.boggle_size + col
            if self.word_trace and self.face_code.get(idx):
                if not idx in self.trace_history:
                    idx2 = self.trace_history[-1]
                    row2 = idx2 // self.boggle_size
                    col2 = idx2 % self.boggle_size
                    if row >= row2 - 1 and row <= row2 + 1 and col >= col2 - 1 and col <= col2 + 1:    
                        self.trace_history.append(idx)
                        self.linked_image_change(idx, idx2)
                        word = ""
                        for idx in self.trace_history:
                            word = word + self.face_decode[self.face_code.get(idx)]
                        self.playerInput.setText(word)     
                    else:
                        self.clear_history()
                        self.playerInput.setText("") 
                else:
                    order = self.trace_history.index(idx)
                    while len(self.trace_history) > order + 1:
                        idx2 = self.trace_history.pop()
                        row2 = idx2 // self.boggle_size
                        col2 = idx2 % self.boggle_size
                        self.dice_faces[row2][col2].setPixmap(QPixmap(
                            self.image_list[self.face_code.get(idx2)]))

                        code = self.link_history.pop()
                        idx2 = self.link_history.pop()
                        row2 = idx2 // self.boggle_size
                        col2 = idx2 % self.boggle_size
                        if code < 300:
                            self.dice_linkh[row2][col2].setPixmap(QPixmap(
                                self.image_list[code]))
                        elif code < 400:
                            self.dice_linkv[row2][col2].setPixmap(QPixmap(
                                self.image_list[code]))
                        else:
                            if code % 100 == 0:
                                self.link_cross_history.remove(idx2)
                            self.dice_linkx[row2][col2].setPixmap(QPixmap(
                                self.image_list[code]))
                    word = ""
                    for idx in self.trace_history:
                        word = word + self.face_decode[self.face_code.get(idx)]
                    self.playerInput.setText(word) 

    def linked_image_change(self, idx, idx2):
        row = idx // self.boggle_size
        col = idx % self.boggle_size
        row2 = idx2 // self.boggle_size
        col2 = idx2 % self.boggle_size
        self.dice_faces[row][col].setPixmap(QPixmap(
            self.image_list[self.face_code.get(idx) + 50]))
        if row == row2:
            if col < col2:
                self.link_history.append(idx)
                self.link_history.append(200)
                self.dice_linkh[row][col].setPixmap(QPixmap(
                    self.image_list[201]))
            else:
                self.link_history.append(idx2)
                self.link_history.append(200)
                self.dice_linkh[row2][col2].setPixmap(QPixmap(
                    self.image_list[201]))
        elif col == col2:
            if row < row2:
                self.link_history.append(idx)
                self.link_history.append(300)
                self.dice_linkv[row][col].setPixmap(QPixmap(
                    self.image_list[301]))
            else:
                self.link_history.append(idx2)
                self.link_history.append(300)
                self.dice_linkv[row2][col2].setPixmap(QPixmap(
                    self.image_list[301]))
        elif col < col2:
            if row < row2:
                if idx in self.link_cross_history:
                    self.link_history.append(idx)
                    self.link_history.append(401)
                    self.dice_linkx[row][col].setPixmap(QPixmap(
                    self.image_list[403]))
                else:
                    self.link_cross_history.append(idx)
                    self.link_history.append(idx)
                    self.link_history.append(400)
                    self.dice_linkx[row][col].setPixmap(QPixmap(
                        self.image_list[401]))
            else:
                link_idx = row2 * self.boggle_size + col
                if link_idx in self.link_cross_history:
                    self.link_history.append(link_idx)
                    self.link_history.append(402)
                    self.dice_linkx[row2][col].setPixmap(QPixmap(
                        self.image_list[403]))
                else:
                    self.link_cross_history.append(link_idx)    
                    self.link_history.append(link_idx)
                    self.link_history.append(400)
                    self.dice_linkx[row2][col].setPixmap(QPixmap(
                        self.image_list[402]))
        else:
            if row < row2:
                link_idx = row * self.boggle_size + col2
                if link_idx in self.link_cross_history:
                    self.link_history.append(link_idx)
                    self.link_history.append(402)
                    self.dice_linkx[row][col2].setPixmap(QPixmap(
                        self.image_list[403]))
                else:
                    self.link_cross_history.append(link_idx)    
                    self.link_history.append(link_idx)
                    self.link_history.append(400)
                    self.dice_linkx[row][col2].setPixmap(QPixmap(
                        self.image_list[402]))
            else:
                if idx2 in self.link_cross_history:
                    self.link_history.append(idx2)
                    self.link_history.append(401)
                    self.dice_linkx[row2][col2].setPixmap(QPixmap(
                        self.image_list[403]))
                else:
                    self.link_cross_history.append(idx2)
                    self.link_history.append(idx2)
                    self.link_history.append(400)
                    self.dice_linkx[row2][col2].setPixmap(QPixmap(
                        self.image_list[401]))

    def clear_history(self):
        self.word_trace = False
        for idx2 in self.trace_history:
            row2 = idx2 // self.boggle_size
            col2 = idx2 % self.boggle_size
            self.dice_faces[row2][col2].setPixmap(QPixmap(
                self.image_list[self.face_code.get(idx2)]))
                        
        while len(self.link_history) > 0:
            code = self.link_history.pop()
            idx2 = self.link_history.pop()
            if code % 100 == 0:
                row2 = idx2 // self.boggle_size
                col2 = idx2 % self.boggle_size
                if code == 200:
                    self.dice_linkh[row2][col2].setPixmap(QPixmap(
                        self.image_list[200]))
                elif code == 300:
                    self.dice_linkv[row2][col2].setPixmap(QPixmap(
                        self.image_list[300]))
                else:
                    self.dice_linkx[row2][col2].setPixmap(QPixmap(
                        self.image_list[400]))

    def input_changed(self, text):
        if len(text) == 0:
            self.word_trace = False
            if len(self.trace_history) == 0:
                return
            idx = self.trace_history[0]
            row = idx // self.boggle_size
            col = idx % self.boggle_size
            self.dice_faces[row][col].setPixmap(QPixmap(
                self.image_list[self.face_code.get(idx)]))
            return
        if not self.word_trace:
            if len(text) == 1:
                if self.face_lookup.get(text.upper()):
                    idx = self.face_lookup[text.upper()][0]
                    row = idx // self.boggle_size
                    col = idx % self.boggle_size
                    self.clicked_letter(row, col)
                else:
                    if self.face_lookup.get("AN") and text.upper() == "A":
                        self.playerInput.setText(text.upper())
                    elif self.face_lookup.get("ER") and text.upper() == "E":
                        self.playerInput.setText(text.upper())
                    elif self.face_lookup.get("HE") and text.upper() == "H":
                        self.playerInput.setText(text.upper())
                    elif self.face_lookup.get("IN") and text.upper() == "I":
                        self.playerInput.setText(text.upper())
                    elif self.face_lookup.get("QU") and text.upper() == "Q":
                        self.playerInput.setText(text.upper())
                    elif self.face_lookup.get("TH") and text.upper() == "T":
                        self.playerInput.setText(text.upper()) 
            elif len(text) == 2:
                if self.face_lookup.get(text.upper()):
                    idx = self.face_lookup[text.upper()][0]
                    row = idx // self.boggle_size
                    col = idx % self.boggle_size
                    self.clicked_letter(row, col)
                else:
                    self.clear_history
                    self.playerInput.setText("")
                    self.word_trace = False
            return

        word = ""
        for idx in self.trace_history:
            word = word + self.face_decode[self.face_code.get(idx)]
        if text == word:
            return
        if len(text) > len(word):
            for idx in range(len(word)):
                if not text[idx] == word[idx]:
                    print("Error " + text + " and " + word)
                    sys.exit() 
            self.extend_string(text[len(word) - len(text):].upper())
        elif len(text) < len(word):
            idx = self.trace_history[-1]
            if len(self.trace_history) > 1:
                idx2 = self.trace_history[-2]
                row = idx2 // self.boggle_size
                col = idx2 % self.boggle_size
                self.linked_letter(row, col)
                if len(self.face_decode[self.face_code[idx]]) > 1:
                    self.playerInput.setText(word[:-1])
                    self.extend_string(self.face_decode[self.face_code[idx]][0])
            elif len(self.trace_history) == 1 and len(text) == 1:
                self.clear_history()
                if self.face_lookup.get(text.upper()):
                    idx = self.face_lookup[text.upper()][0]
                    row = idx // self.boggle_size
                    col = idx % self.boggle_size
                    self.clicked_letter(row, col)
        else:
            print("ERROR 2 " + text + " " + word)

    def extend_string(self, ch):
        if self.face_lookup.get(ch):
            idx2 = self.trace_history[-1]
            row2 = idx2 // self.boggle_size
            col2 = idx2 % self.boggle_size
            for idx in self.face_lookup[ch]:
                if idx in self.trace_history:
                    continue
                row = idx // self.boggle_size
                col = idx % self.boggle_size
                if row >= row2 - 1 and row <= row2 + 1 and col >= col2 - 1 and col <= col2 + 1:    
                    self.linked_letter(row, col)
                    return
        word = ""
        for idx in self.trace_history:
            word = word + self.face_decode[self.face_code.get(idx)]
        self.pending = False
        self.clear_history()
        found = self.matching(word + ch)
        if found:
            self.trace_history = self.new_trace
            self.word_trace = True
            self.refresh_linked_images()
            word = ""
            for idx in self.trace_history:
                word = word + self.face_decode[self.face_code.get(idx)]
            self.playerInput.setText(word)
        elif self.pending:
            self.trace_history = self.backup_trace
            self.word_trace = True
            self.refresh_linked_images()
        else:
            self.playerInput.setText("") 

    def matching(self, text):
        self.new_trace = []
        self.found_word = False
        self.visited = []
        board_size = self.boggle_size * self.boggle_size
        for idx in range(board_size):
            self.visited.append(False)
        
        length = len(text)
        for idx in range(board_size):
            if not self.face_code.get(idx):
                continue
            ch = self.face_decode[self.face_code[idx]]
            count = 0
            matched = True
            for letter in ch:
                if letter == text[count]:
                    count += 1
                else:
                    matched = False
                    break
            if matched:
                self.visited[idx] = True
                self.new_trace.append(idx)
                if len(text) == count:
                    self.found_word = True
                    return True

                self.dfs(text, count, idx, board_size);
                if self.found_word:
                    return True
                self.new_trace.pop()
                self.visited[idx] = False
        return False

    def dfs(self, text, start, lastIdx, board_size):
        if self.found_word:
            retrun
        lastRow = lastIdx // self.boggle_size
        lastCol = lastIdx % self.boggle_size
        for idx in range(board_size):
            if not self.face_code.get(idx) or self.visited[idx]:
                continue
            row = idx // self.boggle_size
            col = idx % self.boggle_size
            if row >= lastRow - 1 and row <= lastRow + 1 and col >= lastCol - 1 and col <= lastCol + 1:
                ch = self.face_decode[self.face_code[idx]]
                count = start
                matched = True
                if not ch[0] == text[count]:
                    continue
                count += 1
                if count == len(text):
                    if len(ch) == 1:
                        self.new_trace.append(idx)
                        self.found_word = True
                        return
                    else:
                        self.pending = True
                        self.backup_trace = []
                        for temp in self.new_trace:
                            self.backup_trace.append(temp)
                        return 
                if len(ch) > 1:
                    if not ch[1] == text[count]:
                        continue
                    count += 1
                    if count == len(text):
                        self.new_trace.append(idx)
                        self.found_word = True
                        return
                self.visited[idx] = True
                self.new_trace.append(idx)
                self.dfs(text, count, idx, board_size)
                if self.found_word:
                    return 
                self.new_trace.pop()
                self.visited[idx] = False
                
    def refresh_linked_images(self):
        self.link_history = []
        self.link_cross_history = []
        idx = self.trace_history[0]
        row = idx // self.boggle_size
        col = idx % self.boggle_size
        self.dice_faces[row][col].setPixmap(QPixmap(
            self.image_list[self.face_code.get(idx) + 50])) 
        for nextIdx in range(1, len(self.trace_history)):
            idx2 = idx
            idx = self.trace_history[nextIdx]
            row = idx // self.boggle_size
            col = idx % self.boggle_size
            self.dice_faces[row][col].setPixmap(QPixmap(
                self.image_list[self.face_code.get(idx) + 50]))
            self.linked_image_change(idx, idx2)

    def input_submitted(self, input_object):
        if self.trace_history == []:
            return
        idx = self.trace_history[-1]
        row = idx // self.boggle_size
        col = idx % self.boggle_size
        self.clicked_letter(row, col)

    def score_3(self, word):
        if word not in self.boggle_words:
            return 0
        length = len(word)
        if length < 3:
            return 0
        if length < 8:
            return self.score_table[length]
        return 11
        
    def score_4(self, word):
        if word not in self.boggle_words:
            return 0
        length = len(word)
        if length < 4:
            return 0
        if length < 8:
            return self.score_table[length]
        return (length - 8) * 2 + 11

    #---------------------------------------

    def custom_quit(self):
        if QMessageBox.question(None, '', 'Are you sure to quit?', 
                    QMessageBox.Yes | QMessageBox.No, QMessageBox.No) == QMessageBox.Yes:
                    QApplication.quit()

    def custom_board(self):
        self.board_window.board_reset()
        self.board_window.select_size(self.boggle_size)
        self.board_window.show()

    def custom_size(self, size):
        self.new_size = size
        self.new_letters = []

    def custom_letter_append(self, code):
        self.new_letters.append(code)

    def custom_ready(self):
        self.game_terminate()
        self.boggle_size = self.new_size
        self.gameNew.setText("New Custom Boggle Board")
        if self.boggle_size < 6:
            self.boggle_time = 3
            self.boggle_scoring = self.score_3
            self.boggle_min_length = 3
        else:
            self.boggle_time = 4
            self.boggle_scoring = self.score_4
            self.boggle_min_length = 4
        self.boggle_resize(self.boggle_size)
        self.game_board = self._gateway.getCustomBoard(self.boggle_size, bytearray(self.new_letters))
        self.game_load()
        self.board_window.hide()

    def dictionary_ospd(self):
        if self._gateway.getInUseDictionary() == "OSPD":
            return
        self._gateway.setDictionaryOspd()
        if self.game_thread.isRunning():
            self.game_terminate()
            self.game_load()

    def dictionary_eowl(self):
        if self._gateway.getInUseDictionary() == "EOWL":
            return
        self._gateway.setDictionaryEowl()
        if self.game_thread.isRunning():
            self.game_terminate()
            self.game_load()

    def dictionary_sowpods(self):
        if self._gateway.getInUseDictionary() == "SOWPODS":
            return
        self._gateway.setDictionarySowpods()
        if self.game_thread.isRunning():
            self.game_terminate()
            self.game_load()

    def dictionary_custom(self):
        filename, _ = QFileDialog.getOpenFileName(self, 'Open File', os.getenv('HOME'))
        if filename == "":
            return
        if not QFile.exists(filename):
            QMessageBox.information(None, 'Error message', 
                    'System error, unable to locate file.',
                    QMessageBox.Close, QMessageBox.Close)
        else:
            self._gateway.setDictionaryCustom(filename)
            if self.game_thread.isRunning():
                self.game_terminate()
                self.game_load()
        
    def cheat_sheet(self):
        words = self._gateway.getWordsList()
        self.list_window = ListingWindow(words)

    def popup_instructions(self):
        QMessageBox.information(None, 'Boggle Game: How to play', 
            '1. Start a new game. 4x4 and 5x5 for 3 minutes and 6x6 for 4 minutes.\n' + 
            '2. Type the word or click the first and last letter.\n\n' + 
            'Change your choice of Boggle size and dictionary from the menu bar.\n' + 
            'Change the dictionary during the game will restart it automatically.\n'
            'You may view the cheat sheet under Help menu.\n\n' + 
            'Have fun!!!', 
                QMessageBox.Close, QMessageBox.Close)

    def about_author(self):
        QMessageBox.information(None, 'About Boggle game', 'Author: Meisze Wong\n' + 
            'www.linkedin.com/pub/macy-wong/46/550/37b/\n\n' + 
            'view source code:\nhttps://github.com/mwong510ca/BoggleGame', 
                    QMessageBox.Close, QMessageBox.Close)
    
    def closeEvent(self, event):
        if self.list_window:
            try:
                self.list_window.close()
            except:
                pass

        if self.board_window:
            try:
                self.board_window.close()
            except:
                pass
        self.closing.emit()
        super(GameBoggle, self).closeEvent(event)
    
if __name__ == "__main__":
    host = '127.0.0.1'
    port_number = 25334
    while port_number < 25335:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.bind(('', 0))
        port_number = s.getsockname()[1]
        s.close()   
    try:
        subprocess.Popen(['java', '-jar', 'BoggleGateway.jar', str(port_number)])
        time.sleep(1)
    except:
        sys.exit()

    gateway = JavaGateway(GatewayClient(address=host, port=port_number))    
    app = QApplication(sys.argv)
    window = GameBoggle(gateway)
    window.show()
    while app.exec_() > 0:
        time.sleep(1)   
    gateway.shutdown() 
    sys.exit()