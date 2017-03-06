"""
" appBoggle is the main GUI application of Boggle game.  It covert the
" actual board game to an application of all three sizes 4x4, 5x5, and 6x6.
"
" User interface created by Qt and implements with pyqt5.  Dictionary supported
" java application.  Default dictionary use OSPD.txt for Scrabble game (US).
" It supported 3 preset dictionaries or any custom word list in
" txt format.  Any words contains special characters will be ignored.
"
" author Meisze Wong
"        www.linkedin.com/pub/macy-wong/46/550/37b/
"        github.com/mwong510ca/Boggle_TrieDataStructure
"""

# !/usr/bin/env python3

import sys
import os
import time
import subprocess
import socket

from PyQt5 import QtCore
from PyQt5.QtWidgets import QApplication, QMainWindow, QMessageBox, QFileDialog
from PyQt5.QtCore import QFile
from PyQt5.QtGui import QPixmap

from gui.mainWindow import Ui_MainWindow as MainWindow
from gui.subCheatSheetWindow import Ui_MainWindow as ListingWindow
from gui.subCustomBoard import BoardWidget
from utilities.gameTimer import Timer
from utilities.gameImages import BoggleImages
from py4j.java_gateway import JavaGateway
from py4j.java_gateway import GatewayClient

# Globals
DICE_4_SIZE = 256
LINK_SIZE = 16
FACE_DECODE = {1: "A", 2: "B", 3: "C", 4: "D", 5: "E", 6: "F",
               7: "G", 8: "H", 9: "I", 10: "J", 11: "K", 12: "L", 13: "M",
               14: "N", 15: "O", 16: "P", 17: "QU", 18: "R", 19: "S", 20: "T",
               21: "U", 22: "V", 23: "W", 24: "X", 25: "Y", 26: "Z",
               101: "AN", 105: "ER", 108: "HE", 109: "IN", 120: "TH"}
SCORE_TABLE = {3: 1, 4: 1, 5: 2, 6: 3, 7: 5}


class GameBoggle(QMainWindow, MainWindow):
    closing = QtCore.pyqtSignal()

    def __init__(self, gateway):
        super().__init__()
        self._gateway = gateway
        self.setupUi(self)

        # mainWindow connection settings
        #    menu bar
        self.actionAboutBoggle.triggered.connect(self.about_boggle)
        self.actionAboutAuthor.triggered.connect(self.about_author)
        self.actionExit.triggered.connect(self.custom_quit)
        self.actionNew_4x4.triggered.connect(lambda: self.boggle_setting(0))
        self.actionClassic_4x4.triggered.connect(lambda: self.boggle_setting(1))
        self.actionDeluxe_5x5.triggered.connect(lambda: self.boggle_setting(2))
        self.actionBig_5x5_1979.triggered.connect(lambda: self.boggle_setting(3))
        self.actionSuperBig_6x6.triggered.connect(lambda: self.boggle_setting(4))
        self.actionCustomBoard.triggered.connect(self.custom_board)
        self.actionOSPD_US.triggered.connect(self.dictionary_ospd)
        self.actionEOWL_UK.triggered.connect(self.dictionary_eowl)
        self.actionSOWPODS.triggered.connect(self.dictionary_sowpods)
        self.actionCustomDictionary.triggered.connect(self.dictionary_custom)
        self.actionInstructions.triggered.connect(self.popup_instructions)
        self.actionCheatSheet.triggered.connect(self.cheat_sheet)
        #    game setup
        self.gameNew.clicked.connect(self.game_start)
        self.playerInput.textChanged.connect(self.input_changed)
        self.playerInput.editingFinished.connect(self.input_submitted)

        # pop-up window for custom board
        self.custom_board_window = BoardWidget()
        self.custom_board_window.boardSize.connect(self.custom_size)
        self.custom_board_window.letterAdded.connect(self.custom_letter_append)
        self.custom_board_window.boardReady.connect(self.custom_ready)

        # pop-up window for cheat sheet
        self.cheat_sheet_window = None

        # boggle board tiles
        #     click function
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
        #     mouseover function
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

        # initial lists
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

        # initial image map
        self.game_images = BoggleImages(FACE_DECODE)

        # size, generate function, game time, score function, min length
        self.board_types = {0: [4, "Boggle (1992)", self._gateway.getNew1992Board, 3, 3],
                            1: [4, "Classic Boggle", self._gateway.getClassicBoard, 3, 3],
                            2: [5, "Boggle Deluxe", self._gateway.getDeluxeBoard, 3, 3],
                            3: [5, "Big Boggle (1979)", self._gateway.getBigBoard, 3, 3],
                            4: [6, "Super Big Boggle", self._gateway.getSuperBigBoard, 4, 4]
                            }
        # game setup
        self.boggle_size = 0
        self.boggle_create = None
        self.boggle_time = 0
        self.boggle_min_length = 0

        self.game_board = None
        self.face_code = {}
        self.face_lookup = {}
        self.boggle_words = []
        self.max_scores = 0
        self.game_thread = Timer()
        self.game_thread.stop()
        self.game_thread.currentTime.connect(self.gameTime.setText)
        self.game_thread.timesUp.connect(self.game_terminate)
        self.game_active = False

        # custom boggle board
        self.selected_size = 0
        self.custom_letter_list = []

        # player input click and mouseover tiles
        self.word_trace = False
        self.trace_history = []
        self.link_history = []
        self.link_cross_history = []

        # player input key-in and backspace
        self.found_path = False
        self.visited_tiles = []
        self.pending_extend_string = False
        self.temporary_trace = []
        self.dfs_new_trace = []

        # player scores and words
        self.game_scores = 0
        self.word_history = []
        self.word_list = []

        self.boggle_setting(1)

    def boggle_resize(self, boggle_size):
        self.actionCheatSheet.setEnabled(False)
        self.playerInput.setEnabled(False)
        self.gameIntro.setText("Change menu setting or start the game.")
        if self.cheat_sheet_window:
            try:
                self.cheat_sheet_window.close()
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
        self.boggle_size = self.board_types[option][0]
        self.gameNew.setText("New " + self.board_types[option][1])
        self.boggle_create = self.board_types[option][2]
        self.boggle_time = self.board_types[option][3]
        self.boggle_min_length = self.board_types[option][4]
        self.boggle_resize(self.boggle_size)

        if self.game_thread.isRunning():
            self.game_thread.stop()
            self.game_terminate()
            self.gameIntro.setText("Boggle board has changed.  Try a new game.")
        self.gameTime.setText("Time:")
        self.gameScores.setText("Scores:")
        self.playerInput.setText("")
        self.game_active = False

    def game_start(self):
        if self.gameNew.text() == "New Custom Boggle Board":
            self.custom_board()
            return
        self.game_board = self.boggle_create()
        self.game_load()

    def game_load(self):
        self.gameIntro.setText("Type the word or click 1st and last letter.")
        self.actionCheatSheet.setEnabled(True)
        self.playerInput.setEnabled(True)
        if self.cheat_sheet_window:
            try:
                self.cheat_sheet_window.close()
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
        self.playerInput.setText("")
        self.wordListDisplay.setText("")
        idx = 0
        for row in range(self.boggle_size):
            for col in range(self.boggle_size):
                code = codes[idx]
                if code > 0:
                    self.face_code[idx] = code
                    phase = FACE_DECODE[code]
                    if self.face_lookup.get(phase):
                        temp = self.face_lookup[phase]
                        temp.append(idx)
                        self.face_lookup[phase] = temp
                    else:
                        self.face_lookup[phase] = [idx]
                self.dice_faces[row][col].setPixmap(QPixmap(self.game_images.getPath(code)))
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
                self.game_images.getPath(self.face_code.get(idx2))))
        self.gameIntro.setText("Time's up.  Try another game.")

    def clicked_letter(self, row, col):
        if self.game_active:
            idx = row * self.boggle_size + col
            if self.face_code.get(idx):
                if self.word_trace:
                    word = ""
                    for idx in self.trace_history:
                        word = word + FACE_DECODE[self.face_code.get(idx)]
                    score = self.get_score(word, self.boggle_min_length)
                    if score > 0 and word not in self.word_list:
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
                            self.game_images.getPath(self.face_code.get(idx) + 50)))
                        self.playerInput.setText(FACE_DECODE[self.face_code.get(idx)])

    def linked_letter(self, row, col):
        if self.game_active:
            idx = row * self.boggle_size + col
            if self.word_trace and self.face_code.get(idx):
                if idx not in self.trace_history:
                    idx2 = self.trace_history[-1]
                    row2 = idx2 // self.boggle_size
                    col2 = idx2 % self.boggle_size
                    if row2 - 1 <= row <= row2 + 1 and col2 - 1 <= col <= col2 + 1:
                        self.trace_history.append(idx)
                        self.linked_image_change(idx, idx2)
                        word = ""
                        for idx in self.trace_history:
                            word = word + FACE_DECODE[self.face_code.get(idx)]
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
                            self.game_images.getPath(self.face_code.get(idx2))))

                        code = self.link_history.pop()
                        idx2 = self.link_history.pop()
                        row2 = idx2 // self.boggle_size
                        col2 = idx2 % self.boggle_size
                        if code < 300:
                            self.dice_linkh[row2][col2].setPixmap(QPixmap(
                                self.game_images.getPath(code)))
                        elif code < 400:
                            self.dice_linkv[row2][col2].setPixmap(QPixmap(
                                self.game_images.getPath(code)))
                        else:
                            if code % 100 == 0:
                                self.link_cross_history.remove(idx2)
                            self.dice_linkx[row2][col2].setPixmap(QPixmap(
                                self.game_images.getPath(code)))
                    word = ""
                    for idx in self.trace_history:
                        word = word + FACE_DECODE[self.face_code.get(idx)]
                    self.playerInput.setText(word)

    def linked_image_change(self, idx, idx2):
        row = idx // self.boggle_size
        col = idx % self.boggle_size
        row2 = idx2 // self.boggle_size
        col2 = idx2 % self.boggle_size
        self.dice_faces[row][col].setPixmap(QPixmap(
            self.game_images.getPath(self.face_code.get(idx) + 50)))
        if row == row2:
            if col < col2:
                self.link_history.append(idx)
                self.link_history.append(200)
                self.dice_linkh[row][col].setPixmap(QPixmap(
                    self.game_images.getPath(201)))
            else:
                self.link_history.append(idx2)
                self.link_history.append(200)
                self.dice_linkh[row2][col2].setPixmap(QPixmap(
                    self.game_images.getPath(201)))
        elif col == col2:
            if row < row2:
                self.link_history.append(idx)
                self.link_history.append(300)
                self.dice_linkv[row][col].setPixmap(QPixmap(
                    self.game_images.getPath(301)))
            else:
                self.link_history.append(idx2)
                self.link_history.append(300)
                self.dice_linkv[row2][col2].setPixmap(QPixmap(
                    self.game_images.getPath(301)))
        elif col < col2:
            if row < row2:
                if idx in self.link_cross_history:
                    self.link_history.append(idx)
                    self.link_history.append(401)
                    self.dice_linkx[row][col].setPixmap(QPixmap(
                        self.game_images.getPath(403)))
                else:
                    self.link_cross_history.append(idx)
                    self.link_history.append(idx)
                    self.link_history.append(400)
                    self.dice_linkx[row][col].setPixmap(QPixmap(
                        self.game_images.getPath(401)))
            else:
                link_idx = row2 * self.boggle_size + col
                if link_idx in self.link_cross_history:
                    self.link_history.append(link_idx)
                    self.link_history.append(402)
                    self.dice_linkx[row2][col].setPixmap(QPixmap(
                        self.game_images.getPath(403)))
                else:
                    self.link_cross_history.append(link_idx)
                    self.link_history.append(link_idx)
                    self.link_history.append(400)
                    self.dice_linkx[row2][col].setPixmap(QPixmap(
                        self.game_images.getPath(402)))
        else:
            if row < row2:
                link_idx = row * self.boggle_size + col2
                if link_idx in self.link_cross_history:
                    self.link_history.append(link_idx)
                    self.link_history.append(402)
                    self.dice_linkx[row][col2].setPixmap(QPixmap(
                        self.game_images.getPath(403)))
                else:
                    self.link_cross_history.append(link_idx)
                    self.link_history.append(link_idx)
                    self.link_history.append(400)
                    self.dice_linkx[row][col2].setPixmap(QPixmap(
                        self.game_images.getPath(402)))
            else:
                if idx2 in self.link_cross_history:
                    self.link_history.append(idx2)
                    self.link_history.append(401)
                    self.dice_linkx[row2][col2].setPixmap(QPixmap(
                        self.game_images.getPath(403)))
                else:
                    self.link_cross_history.append(idx2)
                    self.link_history.append(idx2)
                    self.link_history.append(400)
                    self.dice_linkx[row2][col2].setPixmap(QPixmap(
                        self.game_images.getPath(401)))

    def clear_history(self):
        self.word_trace = False
        for idx2 in self.trace_history:
            row2 = idx2 // self.boggle_size
            col2 = idx2 % self.boggle_size
            self.dice_faces[row2][col2].setPixmap(QPixmap(
                self.game_images.getPath(self.face_code.get(idx2))))

        while len(self.link_history) > 0:
            code = self.link_history.pop()
            idx2 = self.link_history.pop()
            if code % 100 == 0:
                row2 = idx2 // self.boggle_size
                col2 = idx2 % self.boggle_size
                if code == 200:
                    self.dice_linkh[row2][col2].setPixmap(QPixmap(
                        self.game_images.getPath(200)))
                elif code == 300:
                    self.dice_linkv[row2][col2].setPixmap(QPixmap(
                        self.game_images.getPath(300)))
                else:
                    self.dice_linkx[row2][col2].setPixmap(QPixmap(
                        self.game_images.getPath(400)))

    def input_changed(self, text):
        if len(text) == 0:
            self.word_trace = False
            if len(self.trace_history) == 0:
                return
            idx = self.trace_history[0]
            row = idx // self.boggle_size
            col = idx % self.boggle_size
            self.dice_faces[row][col].setPixmap(QPixmap(
                self.game_images.getPath(self.face_code.get(idx))))
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
                    self.clear_history()
                    self.playerInput.setText("")
                    self.word_trace = False
            return

        word = ""
        for idx in self.trace_history:
            word = word + FACE_DECODE[self.face_code.get(idx)]
        if text == word:
            return
        if len(text) > len(word):
            for idx in range(len(word)):
                if not text[idx] == word[idx]:
                    print("Error " + text + " and " + word)
                    sys.exit()
            self.extend_string(text[len(word) - len(text):].upper(), False)
        elif len(text) < len(word):
            word2 = ""
            for idx in range(len(self.trace_history) - 1):
                word2 = word2 + FACE_DECODE[self.face_code.get(self.trace_history[idx])]
            tail_double_letter = False
            if len(word2) + 2 == len(word):
                tail_double_letter = True

            if tail_double_letter:
                first_double = word[-2]
                if len(self.trace_history) > 1:
                    idx = self.trace_history[-2]
                    row = idx // self.boggle_size
                    col = idx % self.boggle_size
                    self.linked_letter(row, col)
                    self.extend_string(first_double, True)
                else:
                    self.clear_history()
                    if self.face_lookup.get(first_double):
                        idx = self.face_lookup[first_double][0]
                        row = idx // self.boggle_size
                        col = idx % self.boggle_size
                        self.clicked_letter(row, col)
                    else:
                        self.playerInput.setText("")
            elif len(self.trace_history) > 1:
                idx = self.trace_history[-1]
                idx2 = self.trace_history[-2]
                row = idx2 // self.boggle_size
                col = idx2 % self.boggle_size
                self.linked_letter(row, col)
                if len(FACE_DECODE[self.face_code[idx]]) > 1:
                    self.playerInput.setText(word[:-1])
                    self.extend_string(FACE_DECODE[self.face_code[idx]][0], False)
            elif len(self.trace_history) == 1 and len(text) == 1:
                self.clear_history()
                if self.face_lookup.get(text.upper()):
                    idx = self.face_lookup[text.upper()][0]
                    row = idx // self.boggle_size
                    col = idx % self.boggle_size
                    self.clicked_letter(row, col)
        else:
            print("ERROR 2 " + text + " " + word)

    def extend_string(self, ch, is_backspace):
        if self.face_lookup.get(ch):
            idx2 = self.trace_history[-1]
            row2 = idx2 // self.boggle_size
            col2 = idx2 % self.boggle_size
            for idx in self.face_lookup[ch]:
                if idx in self.trace_history:
                    continue
                row = idx // self.boggle_size
                col = idx % self.boggle_size
                if row2 - 1 <= row <= row2 + 1 and col2 - 1 <= col <= col2 + 1:
                    self.linked_letter(row, col)
                    return
        word = ""
        for idx in self.trace_history:
            word = word + FACE_DECODE[self.face_code.get(idx)]
        self.pending_extend_string = False
        self.clear_history()
        found = self.search_path(word + ch)
        if found:
            self.trace_history = self.dfs_new_trace
            self.word_trace = True
            self.refresh_linked_images()
            word = ""
            for idx in self.trace_history:
                word = word + FACE_DECODE[self.face_code.get(idx)]
            self.playerInput.setText(word)
        elif self.pending_extend_string:
            self.trace_history = self.temporary_trace
            self.word_trace = True
            self.refresh_linked_images()
        elif not is_backspace:
            self.playerInput.setText("")

    def search_path(self, text):
        self.dfs_new_trace = []
        self.found_path = False
        self.visited_tiles = []
        board_size = self.boggle_size * self.boggle_size
        for idx in range(board_size):
            self.visited_tiles.append(False)

        for idx in range(board_size):
            if not self.face_code.get(idx):
                continue
            ch = FACE_DECODE[self.face_code[idx]]
            count = 0
            matched = True
            for letter in ch:
                if letter == text[count]:
                    count += 1
                else:
                    matched = False
                    break
            if matched:
                self.visited_tiles[idx] = True
                self.dfs_new_trace.append(idx)
                if len(text) == count:
                    self.found_path = True
                    return True

                self.dfs(text, count, idx, board_size)
                if self.found_path:
                    return True
                self.dfs_new_trace.pop()
                self.visited_tiles[idx] = False
        return False

    def dfs(self, text, start, last_idx, board_size):
        if not self.found_path:
            last_row = last_idx // self.boggle_size
            last_col = last_idx % self.boggle_size
            for idx in range(board_size):
                if not self.face_code.get(idx) or self.visited_tiles[idx]:
                    continue
                row = idx // self.boggle_size
                col = idx % self.boggle_size
                if last_row - 1 <= row <= last_row + 1 and last_col - 1 <= col <= last_col + 1:
                    ch = FACE_DECODE[self.face_code[idx]]
                    count = start
                    if not ch[0] == text[count]:
                        continue
                    count += 1
                    if count == len(text):
                        if len(ch) == 1:
                            self.dfs_new_trace.append(idx)
                            self.found_path = True
                            return
                        else:
                            self.pending_extend_string = True
                            self.temporary_trace = []
                            for temp in self.dfs_new_trace:
                                self.temporary_trace.append(temp)
                            return
                    if len(ch) > 1:
                        if not ch[1] == text[count]:
                            continue
                        count += 1
                        if count == len(text):
                            self.dfs_new_trace.append(idx)
                            self.found_path = True
                            return
                    self.visited_tiles[idx] = True
                    self.dfs_new_trace.append(idx)
                    self.dfs(text, count, idx, board_size)
                    if self.found_path:
                        return
                    self.dfs_new_trace.pop()
                    self.visited_tiles[idx] = False

    def refresh_linked_images(self):
        self.link_history = []
        self.link_cross_history = []
        idx = self.trace_history[0]
        row = idx // self.boggle_size
        col = idx % self.boggle_size
        self.dice_faces[row][col].setPixmap(QPixmap(
            self.game_images.getPath(self.face_code.get(idx) + 50)))
        for nextIdx in range(1, len(self.trace_history)):
            idx2 = idx
            idx = self.trace_history[nextIdx]
            row = idx // self.boggle_size
            col = idx % self.boggle_size
            self.dice_faces[row][col].setPixmap(QPixmap(
                self.game_images.getPath(self.face_code.get(idx) + 50)))
            self.linked_image_change(idx, idx2)

    def input_submitted(self):
        if len(self.trace_history) == 0:
            return
        idx = self.trace_history[-1]
        row = idx // self.boggle_size
        col = idx % self.boggle_size
        self.clicked_letter(row, col)

    def get_score(self, word, min_length):
        if word not in self.boggle_words:
            return 0
        length = len(word)
        if length < min_length:
            return 0
        if length < 8:
            return SCORE_TABLE[length]
        if min_length == 3:
            return 11
        else:
            return (length - 8) * 2 + 11

    # ---------------------------------------

    @staticmethod
    def custom_quit():
        if QMessageBox.question(None, '', 'Are you sure to quit?',
                                QMessageBox.Yes | QMessageBox.No, QMessageBox.No) == QMessageBox.Yes:
            QApplication.quit()

    def custom_board(self):
        self.custom_board_window.board_reset()
        self.custom_board_window.select_size(self.boggle_size)
        self.custom_board_window.show()

    def custom_size(self, size):
        self.selected_size = size
        self.custom_letter_list = []

    def custom_letter_append(self, code):
        self.custom_letter_list.append(code)

    def custom_ready(self):
        self.game_terminate()
        self.boggle_size = self.selected_size
        self.gameNew.setText("New Custom Boggle Board")
        if self.boggle_size < 6:
            self.boggle_time = 3
            self.boggle_min_length = 3
        else:
            self.boggle_time = 4
            self.boggle_min_length = 4
        self.boggle_resize(self.boggle_size)
        self.game_board = self._gateway.getCustomBoard(self.boggle_size, bytearray(self.custom_letter_list))
        self.game_load()
        self.custom_board_window.hide()

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
        self.cheat_sheet_window = ListingWindow(words)

    def popup_instructions(self):
        QMessageBox.information(None, 'Boggle Game: How to play',
                                '1. Start a new game. 4x4 and 5x5 for 3 minutes and 6x6 for 4 minutes.\n' +
                                '2. Type the word or click the first and last letter.\n\n' +
                                'Change your choice of Boggle size and dictionary from the menu bar.\n' +
                                'Change the dictionary during the game will restart it automatically.\n'
                                'You may view the cheat sheet under Help menu.\n\n' +
                                'Have fun!!!',
                                QMessageBox.Close, QMessageBox.Close)

    def about_boggle(self):
        QMessageBox.information(None, 'About Boggle game', 'Boggle game based on actual Boggle games \n' +
                                'of all 3 sizes: 4x4, 5x5, and 6x6.\n\n' +
                                'You may use preset dictionaries or a custom dictionary.\n' +
                                'Simply save all word in a text file and upload from setting.\n' +
                                'It\'s good for a kid to play with all words at his/her grade level.\n\n',
                                QMessageBox.Close, QMessageBox.Close)

    def about_author(self):
        QMessageBox.information(None, 'About Boggle game', 'Author: Meisze Wong\n' +
                                'www.linkedin.com/pub/macy-wong/46/550/37b/\n\n' +
                                'view source code:\nwww.github.com/mwong510ca/Boggle_TrieDataStructure',
                                QMessageBox.Close, QMessageBox.Close)

    def closeEvent(self, event):
        if self.cheat_sheet_window:
            try:
                self.cheat_sheet_window.close()
            except:
                pass

        if self.custom_board_window:
            try:
                self.custom_board_window.close()
            except:
                pass
        self.closing.emit()
        super(GameBoggle, self).closeEvent(event)


if __name__ == "__main__":
    host = '127.0.0.1'
    port_number = 25333
    while port_number < 25335:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.bind(('', 0))
        port_number = s.getsockname()[1]
        s.close()
    try:
        p = subprocess.Popen(['java', '-jar', 'BoggleGateway.jar', str(port_number)])
        time.sleep(1)
    except:
        p.kill()
        sys.exit()

    gateway_server = JavaGateway(GatewayClient(address=host, port=port_number))
    app = QApplication(sys.argv)
    window = GameBoggle(gateway_server)
    window.show()
    while app.exec_() > 0:
        time.sleep(1)
    gateway_server.shutdown()
    sys.exit()
