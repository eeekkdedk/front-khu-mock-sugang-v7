"""CI 실패 원인을 GitHub 주석(annotation)으로 남긴다. 로그를 열지 않아도 Actions 요약 화면에서 원인이 보인다.
사용법: python3 scripts/ci-annotate.py <gradle 로그 파일> [추가 로그 파일...]
"""
import glob
import sys
import xml.etree.ElementTree as ET


def esc(s: str) -> str:
    return s.replace('%', '%25').replace('\r', '').replace('\n', '%0A')


def annotate(title: str, body: str) -> None:
    print(f'::error title={esc(title)}::{esc(body[:6000])}')


count = 0
for f in sorted(glob.glob('build/test-results/test/*.xml')):
    root = ET.parse(f).getroot()
    for case in root.iter('testcase'):
        for kind in ('failure', 'error'):
            el = case.find(kind)
            if el is not None and count < 9:
                count += 1
                annotate(f"{root.get('name').split('.')[-1]} > {case.get('name')}",
                         (el.get('message') or '') + '\n' + (el.text or '')[:3000])

for path in sys.argv[1:]:
    try:
        lines = open(path, encoding='utf-8', errors='replace').read().splitlines()
    except FileNotFoundError:
        continue
    important = [l for l in lines if any(k in l for k in ('error:', 'FAILED', 'Caused by', 'What went wrong',
                                                           'Could not', 'APPLICATION FAILED', 'Exception'))]
    annotate(f'log summary: {path}', '\n'.join(important[:60]) + '\n----- tail -----\n' + '\n'.join(lines[-40:]))
