from DocumentCrawler import DocumentCrawler as Crawler
from abc import *
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver import ActionChains
import time
import os

class TeamprojectDocCrawler(Crawler):
    def __init__(self, id, pw):
        self.id = id
        self.pw = pw
        self.driver_path = None
        self.driver = None

    # documentListPage가 존재하지 않아서 접속하지 못한 경우에 false 반환
    def __accessToDocumentListPage(self):
        isDocumentListPageExist = False
        submains = self.driver.find_elements(By.CLASS_NAME, 'submain-notice')
        for i in range(len(submains)):
            submainName = submains[i].find_element(By.CLASS_NAME, 'title').text
            if(submainName.find("팀프로젝트") != -1):
                submains[i].find_element(By.XPATH, './/div[1]').click()
                isDocumentListPageExist = True
                break
        return isDocumentListPageExist

    def createDocumentListByCrawling(self):
        documentList = []

        # 과목 페이지에서 강의자료 리스트 페이지 접속
        if(self.__accessToDocumentListPage() == False):
            return

        time.sleep(0.5)

        # 강의자료 리스트 페이지에서 각 강의자료 페이지에 접속후, 각 요소들을 가져옴.
        subPages = self.driver.find_elements(By.XPATH, '//td[@class="left"]')
        height = subPages[0].size['height']
        # self.driver.execute_script(f"window.scrollTo(0, {height});") # scrolling down을 안하면 클릭이 안되는 문제 발생
        #     height = height*2

        titles = self.driver.find_elements(By.CLASS_NAME, 'subjt_top')
        for i in range(len(titles)):
            title = titles[i]
            if(title.text.find('...') == -1): 
                documentList.append(title.text)
            else: # 제목이 너무 길어서 ... 으로 표시된 경우에는 내부 사이트에 들어가야 함.
                self.driver.execute_script(f"window.scrollTo(0, {height*i});") # scrolling down을 안하면 클릭이 안되는 문제 발생
                subPages[i].click()
                fullTitle = self.driver.find_element(By.CLASS_NAME, 'impt-wrap')
                documentList.append(fullTitle.text)
                self.driver.back()
        return documentList



