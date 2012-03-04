#
# Basic makefile for a LaTeX document
#
# pdf		- pdf output
# clean		- tidy up
#
# Author	- Philipp Fonteyn
# Version	- 1.0.4
# Date		- 12. Oct 2011
#

# Replace this with your file
TEXFILE = probreasoning

pdf : $(TEXFILE).tex

# Create the pdf file
$(TEXFILE).tex : $(TEXFILE).toc $(TEXFILE).bib
	pdflatex $(TEXFILE).tex
	pdflatex $(TEXFILE).tex

# Create the table of contents
$(TEXFILE).toc :
	pdflatex $(TEXFILE).tex

# Compile the references
$(TEXFILE).bib : $(TEXFILE).aux
	bibtex $(TEXFILE)

# Call this with "make clean" to clean up
clean:
	rm -f *.ps *.aux *.toc *.bbl *.blg *.log *.dvi
