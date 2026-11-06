"""Generate an editable Word TDD from docs/technical-design.md using only Python stdlib."""
from html import escape
from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile
import re

ROOT = Path(__file__).resolve().parents[1]
source = ROOT / "docs" / "technical-design.md"
outputs = [ROOT / "Technical-Design-Document.docx", ROOT / "docs" / "technical-design.docx"]


def paragraph(text, style="Normal"):
    text = re.sub(r"\[([^\]]+)\]\(([^)]+)\)", r"\1 (\2)", text)
    text = re.sub(r"\*\*(.*?)\*\*", r"\1", text)
    text = re.sub(r"`([^`]+)`", r"\1", text)
    return (f'<w:p><w:pPr><w:pStyle w:val="{style}"/></w:pPr>'
            f'<w:r><w:t xml:space="preserve">{escape(text)}</w:t></w:r></w:p>')


def table(rows):
    xml = ['<w:tbl><w:tblPr><w:tblStyle w:val="TableGrid"/><w:tblW w:w="0" w:type="auto"/>'
           '<w:tblBorders><w:top w:val="single" w:sz="4" w:color="000000"/>'
           '<w:left w:val="single" w:sz="4" w:color="000000"/>'
           '<w:bottom w:val="single" w:sz="4" w:color="000000"/>'
           '<w:right w:val="single" w:sz="4" w:color="000000"/>'
           '<w:insideH w:val="single" w:sz="4" w:color="000000"/>'
           '<w:insideV w:val="single" w:sz="4" w:color="000000"/></w:tblBorders></w:tblPr>']
    for ri, row in enumerate(rows):
        xml.append('<w:tr>')
        for cell in row:
            value = re.sub(r"\[([^\]]+)\]\(([^)]+)\)", r"\1 (\2)", cell.strip())
            value = re.sub(r"\*\*(.*?)\*\*", r"\1", value)
            value = re.sub(r"`([^`]+)`", r"\1", value)
            shade = ''
            bold = '<w:rPr><w:b/></w:rPr>' if ri == 0 else ''
            xml.append(f'<w:tc><w:tcPr>{shade}<w:tcW w:w="0" w:type="auto"/></w:tcPr>'
                       f'<w:p><w:r>{bold}<w:t xml:space="preserve">{escape(value)}</w:t></w:r></w:p></w:tc>')
        xml.append('</w:tr>')
    xml.append('</w:tbl>')
    return ''.join(xml)


lines = source.read_text(encoding="utf-8-sig").splitlines()
body = []
i = 0
while i < len(lines):
    raw = lines[i]
    line = raw.strip()
    if not line:
        i += 1
        continue
    if line.startswith("```"):
        i += 1
        code = []
        while i < len(lines) and not lines[i].strip().startswith("```"):
            code.append(lines[i])
            i += 1
        body.append(paragraph("  ".join(x.strip() for x in code), "Code"))
        i += 1
        continue
    if line.startswith("| "):
        rows = []
        while i < len(lines) and lines[i].strip().startswith("|"):
            cells = [c.strip() for c in lines[i].strip().strip("|").split("|")]
            if not all(re.fullmatch(r":?-{3,}:?", c or "-") for c in cells):
                rows.append(cells)
            i += 1
        if rows:
            body.append(table(rows))
        continue
    if line.startswith("### "):
        body.append(paragraph(line[4:], "Heading2"))
    elif line.startswith("## "):
        body.append(paragraph(line[3:], "Heading1"))
    elif line.startswith("# "):
        body.append(paragraph(line[2:], "Title"))
    elif line.startswith("- "):
        body.append(paragraph(line[2:], "ListParagraph"))
    elif re.match(r"\d+\. ", line):
        body.append(paragraph(line, "ListParagraph"))
    elif line.startswith("**") and line.endswith("**"):
        body.append(paragraph(line.strip("*"), "Subtitle"))
    else:
        body.append(paragraph(line))
    i += 1

document = ('<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
    '<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body>'
    + ''.join(body) + '<w:sectPr><w:pgSz w:w="12240" w:h="15840"/>'
    '<w:pgMar w:top="900" w:right="960" w:bottom="900" w:left="960"/></w:sectPr>'
    '</w:body></w:document>')
styles = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
<w:style w:type="paragraph" w:default="1" w:styleId="Normal"><w:name w:val="Normal"/><w:rPr><w:rFonts w:ascii="Arial" w:hAnsi="Arial"/><w:sz w:val="22"/><w:color w:val="000000"/></w:rPr><w:pPr><w:spacing w:after="120" w:line="260"/></w:pPr></w:style>
<w:style w:type="paragraph" w:styleId="Title"><w:name w:val="Title"/><w:basedOn w:val="Normal"/><w:rPr><w:b/><w:sz w:val="32"/><w:color w:val="000000"/></w:rPr><w:pPr><w:spacing w:after="200"/></w:pPr></w:style>
<w:style w:type="paragraph" w:styleId="Subtitle"><w:name w:val="Subtitle"/><w:basedOn w:val="Normal"/></w:style>
<w:style w:type="paragraph" w:styleId="Heading1"><w:name w:val="heading 1"/><w:basedOn w:val="Normal"/><w:next w:val="Normal"/><w:rPr><w:b/><w:sz w:val="26"/><w:color w:val="000000"/></w:rPr><w:pPr><w:keepNext/><w:spacing w:before="240" w:after="100"/></w:pPr></w:style>
<w:style w:type="paragraph" w:styleId="Heading2"><w:name w:val="heading 2"/><w:basedOn w:val="Normal"/><w:rPr><w:b/><w:sz w:val="22"/><w:color w:val="000000"/></w:rPr><w:pPr><w:keepNext/><w:spacing w:before="160" w:after="80"/></w:pPr></w:style>
<w:style w:type="paragraph" w:styleId="ListParagraph"><w:name w:val="List Paragraph"/><w:basedOn w:val="Normal"/><w:pPr><w:ind w:left="360" w:hanging="180"/><w:spacing w:after="80"/></w:pPr></w:style>
<w:style w:type="paragraph" w:styleId="Code"><w:name w:val="Code"/><w:basedOn w:val="Normal"/></w:style>
</w:styles>'''
content_types = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/><Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/></Types>'''
rels = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>'''
for target in outputs:
    with ZipFile(target, "w", ZIP_DEFLATED) as doc:
        doc.writestr("[Content_Types].xml", content_types)
        doc.writestr("_rels/.rels", rels)
        doc.writestr("word/document.xml", document)
        doc.writestr("word/styles.xml", styles)
    print(target)
