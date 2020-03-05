import React from 'react';
import { Button, Icon } from 'antd';

class File extends React.Component {
    constructor(props) {
        super(props);
    }
    render() {
        const { allFileList, handleModal, deleteFile } = this.props;
        let renderArr = [];
        for (let key of Object.keys(allFileList)) {
            renderArr.push((
                <div key={key}>
                    <span className="filename">{key}</span>
                    <span className="fileurl">{allFileList[key]}</span>
                    <span onClick={deleteFile.bind(this, key)}>
                        <Icon className="filename-icon" type="close" />
                    </span>
                </div>
            ))
        }
        return (
            <div>
                {renderArr}
                <Button onClick={handleModal}>上传</Button>
            </div>
        )
    }
}

export default File
