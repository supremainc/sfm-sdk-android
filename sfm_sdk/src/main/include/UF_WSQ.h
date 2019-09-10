/**
 *  	WSQ API
 */

/*  
 *  Copyright (c) 2001-2019 Suprema Inc. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of 
 *  Suprema Inc. ("Confidential Information").  You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Suprema.
 */

#ifndef __UNIFINGER_WSQ_H__
#define __UNIFINGER_WSQ_H__

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
//#include <algorithm>

//using namespace std;

#ifndef _WIN32
#define max(x, y) x >= y ? x : y
#endif

#ifndef TRUE
#define TRUE 1
#define FALSE 0
#endif

#define TBLS_N_SOF 2

/* WSQ Marker Definitions */
/******************************************************************
*	X'FFA0' SOI * Start of Image
*	X'FFA1' EOI * End of Image
*	X'FFA2' SOF Start of Frame
*	X'FFA3' SOB Start of Block
*	X'FFA4' DTT Define Transform Table
*	X'FFA5' DQT Define Quantization Table
*	X'FFA6' DHT Define Huffman table(s)
*	X'FFA7' DRI Define Restart Interval
*	X'FFB0' - X'FFB7' RSTm* Restart with modulo 8 count ��m��
*	X'FFA8' COM Comment
******************************************************************/

#define SOI_WSQ 0xffa0 //Start of Image
#define EOI_WSQ 0xffa1 //End of Image
#define SOF_WSQ 0xffa2 //Start of Frame
#define SOB_WSQ 0xffa3 //Start of Block
#define DTT_WSQ 0xffa4 //Define Transform Table
#define DQT_WSQ 0xffa5 //Define Quantization Table
#define DHT_WSQ 0xffa6 //Define Huffman table(s)
#define DRT_WSQ 0xffa7 //Define Restart Interval
#define COM_WSQ 0xffa8 //Comment

/* Case for getting ANY marker. */
#define ANY_WSQ 0xffff
#define TBLS_N_SOB (TBLS_N_SOF + 2)

// ??????
//#define FILTBANK_EVEN_8X8_1

/* Filter Bank Definitions */
#ifdef FILTBANK_EVEN_8X8_1
#define MAX_HIFILT 8
#define MAX_LOFILT 8
#else
#define MAX_HIFILT 7
#define MAX_LOFILT 9
#endif

/* Subband Definitions */
#define STRT_SUBBAND_2 19
#define STRT_SUBBAND_3 52
#define MAX_SUBBANDS 64
#define NUM_SUBBANDS 60
#define STRT_SUBBAND_DEL (NUM_SUBBANDS)
#define STRT_SIZE_REGION_2 4
#define STRT_SIZE_REGION_3 51
#define MIN_IMG_DIM 256
#define WHITE 255
#define BLACK 0
#define COEFF_CODE 0
#define RUN_CODE 1
#define RAW_IMAGE 1
#define IHEAD_IMAGE 0
#define VARIANCE_THRESH 1.01

#define NCM_PPI "PPI" /* -1 if unknown (manditory)*/

/* nistcom.h */
#define NCM_HEADER "NIST_COM" /* manditory */

/* jpegl.h */
#define READ_TABLE_LEN 1
#define NO_READ_TABLE_LEN 0

/* wsq.h */
#define MAXFETS 100
#define W_TREELEN 20
#define Q_TREELEN 64
#define MAX_DHT_TABLES 8

/* Defined in jpegl.h */
#define MAX_HUFFBITS 16
#define MAX_HUFFCOUNTS_WSQ 256 /* Length of code table: change as needed */
/* but DO NOT EXCEED 256 */
#define MAX_HUFFCOEFF 74 /* -73 .. +74 */
#define MAX_HUFFZRUN 100
#define MAXFETLENGTH 512

/* nistcom.h */
#define NCM_COLORSPACE "COLORSPACE"   /* RGB,YCbCr,GRAY */
#define NCM_COMPRESSION "COMPRESSION" /* NONE,JPEGB,JPEGL,WSQ */
#define NCM_WSQ_RATE "WSQ_BITRATE"	/* ex. .75,2.25 (-1.0 if unknown)*/
#define NCM_PIX_WIDTH "PIX_WIDTH"	 /* manditory */
#define NCM_PIX_HEIGHT "PIX_HEIGHT"   /* manditory */
#define NCM_PIX_DEPTH "PIX_DEPTH"	 /* 1,8,24 (manditory)*/
#define NCM_LOSSY "LOSSY"			  /* 0,1 */

/* defs.h */
#define sround(x) ((int)(((x) < 0) ? (x)-0.5 : (x) + 0.5))
#define sround_uint(x) ((unsigned int)(((x) < 0) ? (x)-0.5 : (x) + 0.5))

/* Defined in swap.h */
#define swap_short_bytes(_a_)     \
	{                             \
		short _b_ = _a_;          \
		char *_f_ = (char *)&_b_; \
		char *_t_ = (char *)&_a_; \
		_t_[1] = _f_[0];          \
		_t_[0] = _f_[1];          \
	}

#define swap_int_bytes(_ui_)                           \
	{                                                  \
		int _b_ = _ui_;                                \
		unsigned char *_f_ = (unsigned char *)&(_b_);  \
		unsigned char *_t_ = (unsigned char *)&(_ui_); \
		_t_[3] = _f_[0];                               \
		_t_[2] = _f_[1];                               \
		_t_[1] = _f_[2];                               \
		_t_[0] = _f_[3];                               \
	}

typedef struct header_frm
{
	unsigned char black;
	unsigned char white;
	unsigned short width;
	unsigned short height;
	float m_shift;
	float r_scale;
	unsigned char wsq_encoder;
	unsigned short software;
} FRM_HEADER_WSQ;

typedef struct table_dtt
{
	float *lofilt;
	float *hifilt;
	unsigned char losz;
	unsigned char hisz;
	char lodef;
	char hidef;
} DTT_TABLE;

typedef struct table_dqt
{
	float bin_center;
	float q_bin[MAX_SUBBANDS];
	float z_bin[MAX_SUBBANDS];
	char dqt_def;
} DQT_TABLE;

typedef struct wavlet_tree
{
	int x;
	int y;
	int lenx;
	int leny;
	int inv_rw;
	int inv_cl;
} W_TREE;

typedef struct quant_tree
{
	short x; /* UL corner of block */
	short y;
	short lenx; /* block size */
	short leny; /* block size */
} Q_TREE;

typedef struct fetstruct
{
	int alloc;
	int num;
	char **names;
	char **values;
} FET;

typedef FET NISTCOM;

typedef struct hcode
{
	short size;
	unsigned int code;
} HUFFCODE;

typedef struct table_dht
{
	unsigned char tabdef;
	unsigned char huffbits[MAX_HUFFBITS];
	unsigned char huffvalues[MAX_HUFFCOUNTS_WSQ + 1];
} DHT_TABLE;

typedef struct quantization
{
	float q;  /* quantization level */
	float cr; /* compression ratio */
	float r;  /* compression bitrate */
	float qbss_t[MAX_SUBBANDS];
	float qbss[MAX_SUBBANDS];
	float qzbs[MAX_SUBBANDS];
	float var[MAX_SUBBANDS];
} QUANT_VALS;

#ifdef __cplusplus
extern "C"
{
#endif

	int wsq_decode_file(unsigned char **odata, int *ow, int *oh, int *od, int *oppi, int *lossyflag, FILE *infp);
	int wsq_decode_mem(unsigned char **odata, int *ow, int *oh, int *od, int *oppi, int *lossyflag, unsigned char *idata, const int ilen);

	int wsq_encode_mem(unsigned char **odata, int *olen, const float r_bitrate, unsigned char *idata, const int w, const int h, const int d, const int ppi, char *comment_text);

	int write_raw_from_memsize(char *ofile, unsigned char *odata, const int olen);

	/* encoder.c */
	int gen_hufftable_wsq(HUFFCODE **ohufftable, unsigned char **ohuffbits, unsigned char **ohuffvalues, short *sip, const int *block_sizes, const int num_sizes);
	int compress_block(unsigned char *outbuf, int *obytes, short *sip, const int sip_siz, const int MaxCoeff, const int MaxZRun, HUFFCODE *codes);
	int count_block(int **ocounts, const int max_huffcounts, short *sip, const int sip_siz, const int MaxCoeff, const int MaxZRun);
	/* tree.c */
	void build_wsq_trees(W_TREE w_tree[], const int w_treelen, Q_TREE q_tree[], const int q_treelen, const int width, const int height);
	void build_w_tree(W_TREE w_tree[], const int width, const int height);
	void build_q_tree(W_TREE *w_tree, Q_TREE *q_tree);
	void q_tree4(Q_TREE *q_tree, const int start, const int lenx, const int leny, const int x, const int y);
	void w_tree4(W_TREE w_tree[], const int start1, const int start2, const int lenx, const int leny, const int x, const int y, const int stop1);
	void q_tree16(Q_TREE *q_tree, const int start, const int lenx, const int leny, const int x, const int y, const int rw, const int cl);
	/* tableio.c */
	int read_table_wsq(unsigned short marker, DTT_TABLE *dtt_table, DQT_TABLE *dqt_table, DHT_TABLE *dht_table, FILE *infp);
	int read_frame_header_wsq(FRM_HEADER_WSQ *frm_header, FILE *infp);
	int read_marker_wsq(unsigned short *omarker, const int type, FILE *infp);
	int read_transform_table(DTT_TABLE *dtt_table, FILE *infp);
	int read_quantization_table(DQT_TABLE *dqt_table, FILE *infp);
	int read_huffman_table_wsq(DHT_TABLE *dht_table, FILE *infp);
	int read_block_header(unsigned char *huff_table, FILE *infp);
	int getc_marker_wsq(unsigned short *omarker, const int type, unsigned char **cbufptr, unsigned char *ebufptr);
	int getc_table_wsq(unsigned short marker, DTT_TABLE *dtt_table, DQT_TABLE *dqt_table, DHT_TABLE *dht_table, unsigned char **cbufptr, unsigned char *ebufptr);
	int getc_frame_header_wsq(FRM_HEADER_WSQ *frm_header, unsigned char **cbufptr, unsigned char *ebufptr);
	int getc_nistcom_wsq(NISTCOM **onistcom, unsigned char *idata, const int ilen);
	int getc_transform_table(DTT_TABLE *dtt_table, unsigned char **cbufptr, unsigned char *ebufptr);
	int getc_quantization_table(DQT_TABLE *dqt_table, unsigned char **cbufptr, unsigned char *ebufptr);
	int getc_huffman_table_wsq(DHT_TABLE *dht_table, unsigned char **cbufptr, unsigned char *ebufptr);
	int getc_block_header(unsigned char *huff_table, unsigned char **cbufptr, unsigned char *ebufptr);
	int putc_transform_table(float *lofilt, const int losz, float *hifilt, const int hisz, unsigned char *odata, const int oalloc, int *olen);
	int putc_nistcom_wsq(char *comment_text, const int w, const int h, const int d, const int ppi, const int lossyflag, const float r_bitrate, unsigned char *odata, const int oalloc, int *olen);
	int putc_quantization_table(QUANT_VALS *quant_vals, unsigned char *odata, const int oalloc, int *olen);
	int putc_frame_header_wsq(const int width, const int height, const float m_shift, const float r_scale, unsigned char *odata, const int oalloc, int *olen);
	int putc_block_header(const int table, unsigned char *odata, const int oalloc, int *olen);
	/* tableio.c 2*/
	int read_nistcom_wsq(NISTCOM **onistcom, FILE *infp);
	int getc_comment(unsigned char **ocomment, unsigned char **cbufptr, unsigned char *ebufptr);
	int read_comment(unsigned char **ocomment, FILE *infp);
	int putc_comment(const unsigned short marker, unsigned char *comment, const int cs, unsigned char *odata, const int oalloc, int *olen);
	/* extrfet.c */
	int extractfet_ret(char **ovalue, char *feature, FET *fet);
	/* freefet.c */
	void freefet(FET *fet);
	/* util.c */
	int wsq_reconstruct(float *fdata, const int width, const int height, W_TREE w_tree[], const int w_treelen, const DTT_TABLE *dtt_table);
	int unquantize(float **ofip, const DQT_TABLE *dqt_table, Q_TREE q_tree[], const int q_treelen, short *sip, const int width, const int height);
	void init_wsq_decoder_resources();
	void free_wsq_decoder_resources();
	void conv_img_2_flt(float *fip, float *m_shift, float *r_scale, unsigned char *data, const int num_pix);
	int wsq_decompose(float *fdata, const int width, const int height, W_TREE w_tree[], const int w_treelen, float *hifilt, const int hisz, float *lofilt, const int losz);
	void variance(QUANT_VALS *quant_vals, Q_TREE q_tree[], const int q_treelen, float *fip, const int width, const int height);
	int quantize(short **osip, int *ocmp_siz, QUANT_VALS *quant_vals, Q_TREE q_tree[], const int q_treelen, float *fip, const int width, const int height);
	void quant_block_sizes(int *oqsize1, int *oqsize2, int *oqsize3, QUANT_VALS *quant_vals, W_TREE w_tree[], const int w_treelen, Q_TREE q_tree[], const int q_treelen);
	void get_lets(float *new_, float *old, const int len1, const int len2, const int pitch, const int stride, float *hi, const int hsz, float *lo, const int lsz, const int inv);
	/* huff.c */
	int check_huffcodes_wsq(HUFFCODE *hufftable, int last_size);
	int getc_huffman_table(unsigned char *otable_id, unsigned char **ohuffbits, unsigned char **ohuffvalues, const int max_huffcounts, unsigned char **cbufptr, unsigned char *ebufptr, const int read_table_len, int *bytes_left);
	int putc_huffman_table(const unsigned short marker, const unsigned char table_id, unsigned char *huffbits, unsigned char *huffvalues, unsigned char *outbuf, const int outalloc, int *outlen);
	int find_huff_sizes(int **ocodesize, int *freq, const int max_huffcounts);
	int find_num_huff_sizes(unsigned char **obits, int *adjust, int *codesize, const int max_huffcounts);
	int sort_huffbits(unsigned char *bits);
	int sort_code_sizes(unsigned char **ovalues, int *codesize, const int max_huffcounts);
	int build_huffcode_table(HUFFCODE **ohuffcode_table, HUFFCODE *in_huffcode_table, const int last_size, unsigned char *values, const int max_huffcounts);
	void find_least_freq(int *value1, int *value2, int *freq, const int max_huffcounts);
	/* huff.c jpg */
	void gen_decode_table(HUFFCODE *huffcode_table, int *maxcode, int *mincode, int *valptr, unsigned char *huffbits);
	void build_huffcodes(HUFFCODE *huffcode_table);
	int build_huffsizes(HUFFCODE **ohuffcode_table, int *temp_size, unsigned char *huffbits, const int max_huffcounts);
	int read_huffman_table(unsigned char *otable_id, unsigned char **ohuffbits, unsigned char **ohuffvalues, const int max_huffcounts, FILE *infp, const int read_table_len, int *bytes_left);
	/* decoder.c */
	int decode_data_file(int *onodeptr, int *mincode, int *maxcode, int *valptr, unsigned char *huffvalues, FILE *infp, int *bit_count, unsigned short *marker);
	int decode_data_mem(int *onodeptr, int *mincode, int *maxcode, int *valptr, unsigned char *huffvalues, unsigned char **cbufptr, unsigned char *ebufptr, int *bit_count, unsigned short *marker);
	int huffman_decode_data_mem(short *ip, DTT_TABLE *dtt_table, DQT_TABLE *dqt_table, DHT_TABLE *dht_table, unsigned char **cbufptr, unsigned char *ebufptr);
	int huffman_decode_data_file(short *ip, DTT_TABLE *dtt_table, DQT_TABLE *dqt_table, DHT_TABLE *dht_table, FILE *infp);
	int getc_nextbits_wsq(unsigned short *obits, unsigned short *marker, unsigned char **cbufptr, unsigned char *ebufptr, int *bit_count, const int bits_req);
	int nextbits_wsq(unsigned short *obits, unsigned short *marker, FILE *file, int *bit_count, const int bits_req);
	/* dataio.c */
	int read_uint(unsigned int *oint_dat, FILE *infp);
	int read_ushort(unsigned short *oshrt_dat, FILE *infp);
	int read_byte(unsigned char *ochar_dat, FILE *infp);
	int getc_uint(unsigned int *oint_dat, unsigned char **cbufptr, unsigned char *ebufptr);
	int getc_ushort(unsigned short *oshrt_dat, unsigned char **cbufptr, unsigned char *ebufptr);
	int getc_byte(unsigned char *ochar_dat, unsigned char **cbufptr, unsigned char *ebufptr);
	int getc_bytes(unsigned char **ochar_dat, const int ilen, unsigned char **cbufptr, unsigned char *ebufptr);
	int putc_ushort(unsigned short ishort, unsigned char *odata, const int oalloc, int *olen);
	int putc_bytes(unsigned char *idata, const int ilen, unsigned char *odata, const int oalloc, int *olen);
	int putc_byte(const unsigned char idata, unsigned char *odata, const int oalloc, int *olen);
	int putc_uint(unsigned int iint, unsigned char *odata, const int oalloc, int *olen);
	void write_bits(unsigned char **outbuf, const unsigned short code, const short size, int *outbit, unsigned char *bits, int *bytes);
	void flush_bits(unsigned char **outbuf, int *outbit, unsigned char *bits, int *bytes);
	/* util.c */
	int int_sign(const int power);
	void conv_img_2_uchar(unsigned char *data, float *img, const int width, const int height, const float m_shift, const float r_scale);
	void join_lets(float *news, float *old, const int len1, const int len2, const int pitch, const int stride, float *hi, const int hsz, float *lo, const int lsz, const int inv);
	/* updatfet.c */
	int updatefet_ret(char *feature, char *value, FET *fet);
	/* strfet.c */
	int string2fet(FET **ofet, char *istr);
	/* computil.c */
	int read_skip_marker_segment(const unsigned short marker, FILE *infp);
	int getc_skip_marker_segment(const unsigned short marker, unsigned char **cbufptr, unsigned char *ebufptr);
	/* allocfet.c */
	int allocfet_ret(FET **ofet, int numfeatures);
	int reallocfet_ret(FET **ofet, int newlen);
	/* ppi.c */
	int getc_ppi_wsq(int *oppi, unsigned char *idata, const int ilen);
	int read_ppi_wsq(int *oppi, FILE *infp);
	/* strfet.c */
	int fet2string(char **ostr, FET *fet);
	/* lkupfet.c */
	int lookupfet(char **ovalue, char *feature, FET *fet);
	/* nistcom.c */
	int combine_wsq_nistcom(NISTCOM **onistcom, const int w, const int h, const int d, const int ppi, const int lossyflag, const float r_bitrate);
	int combine_nistcom(NISTCOM **onistcom, const int w, const int h, const int d, const int ppi, const int lossyflag);

#ifdef __cplusplus
}
#endif

#endif // __i386__
