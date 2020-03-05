import React from 'react';
import PropTypes from 'prop-types';
import { Row, Col, Slider, Button, Input } from 'antd';
import AvatarEditor from 'react-avatar-editor';
import '../../styles/cms/imgEditor.less';


class ImgEditor extends React.Component {
    constructor(props) {
        super(props);
        this.state = this.getInitState();
    }

    getInitState() {
        return {
            position: { x: 0.5, y: 0.5 },
            rotate: 0,
            scale: 1,
            w_r: 0,
            h_r: 0,
            w_m: 600,
            h_m: 600,
            w_c: 600,
            h_c: 600
        };
    }

    getFinalSize = () => {
        const { width, height, ratio } = this.props;
        const { position, scale, rotate, w_r, h_r, w_m, h_m, w_c, h_c } = this.state;

        const size = { w_f: w_r, h_f: h_r, w_m, h_m, w_c, h_c, scale };

        const px = position.x;
        const py = position.y;

        size.px = px;
        size.py = py;

        // const sx = parseInt((w_c / 2) * (scale - 1) + w_c * scale * (px - 0.5));
        // const sy = parseInt((h_c / 2) * (scale - 1) + h_c * scale * (py - 0.5));
        // const sx = parseInt(w_r * (scale * px - 0.5) / scale);
        // const sy = parseInt(h_r * (scale * py - 0.5) / scale);
        const sx = parseInt(w_m * scale * px - w_c * 0.5);
        const sy = parseInt(h_m * scale * py - h_c * 0.5);

        size.sx = sx;
        size.sy = sy;

        if (width) {
            size.w_f = width;
        }

        if (!width && ratio && height) {
            size.w_f = parseInt(height * ratio);
        }

        if (height) {
            size.h_f = height;
        }

        if (!height && ratio && width) {
            size.h_f = parseInt(width / ratio);
        }

        if (!width && !height && ratio) {
            size.w_f = parseInt(w_r / scale);
            size.h_f = parseInt(size.w_f / ratio);
        }

        if (!width && !height && !ratio) {
            size.w_f = parseInt(w_r / scale);
            size.h_f = parseInt(h_r / scale);
        }
        return size;
    }

    imgLoadSuccess = ({ resource: { width: w_r, height: h_r } }) => {
        let w_c = 600, h_c = 600, w_m = 600, h_m = 600;
        let { width, height, ratio } = this.props;
        ratio = parseFloat(ratio);
        if (width && height) {
            width = +width;
            height = +height;
        } else if (width && !height && ratio) {
            height = parseInt(width / ratio);
        } else if (height && !width && ratio) {
            width = parentInt(height * ratio);
        } else if (!width && !height && ratio) {
            width = ratio > 1 ? w_c : parseInt(w_c * ratio);
            height = ratio > 1 ? parseInt(h_c / ratio) : h_c;
        } else {
            width = w_r;
            height = h_r;
        }

        ratio = width / height;

        if (width > w_c || height > h_c) {
            if (ratio > 1) {
                h_c = parseInt(h_c / ratio);
            } else {
                w_c = parseInt(w_c * ratio);
            }
        } else {
            w_c = width;
            h_c = height;
        }

        const ratio_r = w_r / h_r;

        if (ratio > ratio_r) {
            w_m = w_c;
            h_m = parseInt(w_c * h_r / w_r);
        } else {
            w_m = parseInt(w_r * h_c / h_r);
            h_m = h_c;
        }

        this.setState({
            w_c,
            h_c,
            w_m,
            h_m,
            w_r,
            h_r
        });
    }

    posChg = (position) => {
        this.setState({
            position
        });
    }

    scaleChg = (scale) => {
        this.setState({
            scale
        });
    }

    onSave = () => {
        let { imgId, imgUrl, imgSave } = this.props;
        let urlBase = imgUrl.split("?x-oss-process")[0];
        const { sx, sy, w_c, h_c, w_m, h_m, w_f, h_f, scale } = this.getFinalSize();
        if (imgUrl.indexOf('?x-oss-process=image') < 0) {
            urlBase += '?x-oss-process=image';
        } else {
            urlBase = imgUrl;
        }
        imgUrl = urlBase + `/resize,m_mfit,limit_0,w_${parseInt(w_m * scale)},h_${parseInt(h_m * scale)}/crop,x_${sx},y_${sy},w_${w_c},h_${h_c}/resize,m_mfit,limit_0,w_${w_f},h_${h_f}`;
        // imgUrl = urlBase + `/crop,x_${sx},y_${sy},w_${w_f},h_${h_f}/resize,m_fixed,limit_0,w_${w_f},h_${h_f}`;
        imgSave(imgId, imgUrl);
        setTimeout(() => {
            this.state = this.getInitState();
        });
    }

    render() {
        const { position, rotate, scale, w_c, h_c } = this.state;
        const { width, height, ratio, imgUrl, imgSave, imgCancel } = this.props;
        return (
            <div className="img-editor">
                <AvatarEditor
                    onPositionChange={this.posChg}
                    position={position}
                    rotate={rotate}
                    border={0}
                    image={imgUrl}
                    width={w_c}
                    height={h_c}
                    scale={scale}
                    onLoadSuccess={this.imgLoadSuccess}
                />
                <div className="op-box">
                    <Row>
                        <Col span={4} className="label-col">缩放：</Col>
                        <Col span={20}>
                            <Slider
                                min={1}
                                max={10}
                                step={0.1}
                                value={scale}
                                onChange={this.scaleChg}
                            />
                        </Col>
                    </Row>
                </div>
                <div className="btn-box">
                    <Button type="primary" onClick={this.onSave}>确定</Button>
                    <Button onClick={imgCancel}>取消</Button>
                </div>
            </div>
        );
    }
}

export default ImgEditor;
